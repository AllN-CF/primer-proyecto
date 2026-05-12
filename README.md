**Sistema de Gestión de Ventas (API REST)**

API desarrollada con Spring Boot 3 para la administración eficiente de transacciones y control de inventarios.


🛠️ **Tecnologías y Herramientas**

- Lenguaje: Java 21
- Framework: Spring Boot 3 (JPA, Spring Security)
- Base de Datos: PostgreSQL / H2 (Pruebas)
- Gestor de Dependencias: Maven

🏗️ **Arquitectura**

El proyecto utiliza una Arquitectura en Capas para asegurar el desacoplamiento:

- Controllers: Definición de Endpoints RESTful.
- Services: Lógica de negocio y validaciones.
- Repositories: Persistencia de datos mediante Spring Data JPA.
- DTOs: Objetos de transferencia para mayor seguridad y control de datos expuestos.

🔒 **Seguridad**

- Manejo de Errores: Implementación de @RestControllerAdvice para una gestión global de excepciones.
- Autenticación (En desarrollo): Implementación de seguridad apátrida mediante JWT (Actualmente en proceso de integración).
