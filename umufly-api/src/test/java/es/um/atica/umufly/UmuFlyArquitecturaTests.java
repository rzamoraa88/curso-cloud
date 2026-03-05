package es.um.atica.umufly;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;
import static com.tngtech.archunit.library.GeneralCodingRules.NO_CLASSES_SHOULD_USE_FIELD_INJECTION;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaMethod;
import com.tngtech.archunit.core.domain.JavaModifier;
import com.tngtech.archunit.core.domain.JavaParameter;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;

import jakarta.validation.Valid;

@AnalyzeClasses( packages = "es.um.atica.umufly" )
public class UmuFlyArquitecturaTests {

	@ArchTest
	static final ArchRule ninguna_interfaz_acaba_en_impl = noClasses().that().areInterfaces().should().haveSimpleNameEndingWith( "Impl" ).because( "Las interfaces no deben acabar por Impl" );

	@ArchTest
	static final ArchRule codigo_respeta_arquitectura_hexagonal = layeredArchitecture()// Define una estructura por capas
	.consideringAllDependencies() // Cojo todas las dependencias del proyecto
	.layer( "Domain" ).definedBy( "..domain.." ) // Defino la capa de dominio por todas las clases dentro de ..domain..
	.layer( "Application" ).definedBy( "..application.." ) // Defino la capa de aplicacion por todas las clases dentro de ..application..
	.layer( "Adapters" ).definedBy( "..adaptors.." )// Defino la capa de adaptadores por todas las clases dentro de ..adaptors..
	.whereLayer( "Domain" ).mayOnlyBeAccessedByLayers( "Application", "Adapters" ) // Indico que "imports" de la
	// capa de dominio pueden estar en Application y Adapters
	.whereLayer( "Application" ).mayOnlyBeAccessedByLayers( "Adapters" )// Indico que "imports" de la capa de
	// adaptadores pueden estar en Application (pero no en dominio)
	.whereLayer( "Adapters" ).mayNotBeAccessedByAnyLayer(); // Ninguna capa debe tener import de adapters

	private static final ArchCondition<JavaMethod> METODO_REST_VALIDA_PARAMETROS = new ArchCondition<>( "Rest debe tener @Valid o @Validated en parámetros @RequestBody" ) {

		@Override
		public void check( JavaMethod metodo, ConditionEvents events ) {
			boolean validaParametro = false;
			for ( JavaParameter parametro : metodo.getParameters() ) {
				// Compruebo si el metodo tiene RequestBody (JSON)
				if ( parametro.isAnnotatedWith( RequestBody.class ) ) {
					validaParametro = parametro.isAnnotatedWith( Valid.class ) || parametro.isAnnotatedWith( Validated.class ) || metodo.getOwner().isAnnotatedWith( Validated.class );
					if ( !validaParametro ) {
						String message = String.format( "El método %s tiene un @RequestBody sin validación", metodo.getFullName() );
						// Aniado un evento de violacion
						events.add( SimpleConditionEvent.violated( metodo, message ) );
					}
				}
			}
		}
	};

	private static ArchCondition<JavaClass> superaMaximoMetodosPublicos( int max ) {
		return new ArchCondition<>( "tener máximo " + max + " métodos públicos" ) {

			@Override
			public void check( JavaClass item, ConditionEvents events ) {
				long count = item.getMethods().stream().filter( m -> m.getModifiers().contains( JavaModifier.PUBLIC ) ).count();

				if ( count > max ) {
					String message = String.format( "La clase %s tiene %d métodos públicos", item.getFullName(), count );
					events.add( SimpleConditionEvent.violated( item, message ) );
				}
			}
		};
	}

	private static final DescribedPredicate<JavaClass> IMPLEMENTA_ALGUNA_INTERFAZ = new DescribedPredicate<JavaClass>( "implementa al menos una interfaz" ) {

		@Override
		public boolean test( JavaClass t ) {
			return !t.getInterfaces().isEmpty();
		}
	};

	@ArchTest
	static final ArchRule api_rest_debe_validar_datos_entrada = methods().that().areDeclaredInClassesThat().areAnnotatedWith( RestController.class ).and().arePublic().should( METODO_REST_VALIDA_PARAMETROS );
	// codigo_system_out
	// codigo_print_stacktrace
	// @ArchTest
	// static final ArchRule codigo_print_stacktrace = noClasses().should().callMethod( null, null, null )
	// handler_anotado_por_component
	// sin_inyeccion_autowired
	@ArchTest
	static final ArchRule sin_inyeccion_autowired = NO_CLASSES_SHOULD_USE_FIELD_INJECTION.because( "" );
	// Las implementaciones de interfaces deben acabar en Impl
	@ArchTest
	static final ArchRule implementaciones_interfaces_acaben_impl = classes().that( IMPLEMENTA_ALGUNA_INTERFAZ ).should().haveSimpleNameEndingWith( "Impl" ).allowEmptyShould( true );
	// Los DTO acaban en DTO
	@ArchTest
	static final ArchRule dtos_acaban_en_dto = classes().that().resideInAPackage( "..dto.." ).should().haveSimpleNameEndingWith( "DTO" ).allowEmptyShould( true );
	// Los RestController solo pueden estar en la capa de adaptadores
	@ArchTest
	static final ArchRule restcontrollers_estan_en_capa_adaptadores = classes().that().areAnnotatedWith( RestController.class ).should().resideInAPackage( "..adaptors.." ).as( "Los controladores REST tienen que estar en la capa adaptors" );
	// Ninguna clase debe tener más de 20 métodos públicos
	@ArchTest
	static final ArchRule ninguna_clase_mas_20_metodos_publicos = classes().should( superaMaximoMetodosPublicos( 20 ) ).because( "Ninguna clase debe tener más de 20 métodos públicos" );
}
