# 🚗 ParkingAXM

**Sistema de Gestión de Parqueaderos – JavaFX**

ParkingAXM es una aplicación de escritorio desarrollada en **JavaFX** y **Maven**, diseñada para administrar un parqueadero con control de usuarios, registro de vehículos, cobro por tiempo y estadísticas básicas.
El enfoque principal del proyecto es lograr una **arquitectura limpia, modular y funcional**, antes de aplicar diseño o estilos.

---

## 📌 Funcionalidades principales

* **Login con roles**

    * **Administrador:** acceso total y creación de nuevos usuarios.
    * **Operario:** solo puede listar vehículos y dar salida.
* **Registro de vehículos**

    * El usuario ingresa la placa → el sistema registra automáticamente fecha/hora.
    * Control de espacios disponibles sin asignación manual.
* **Cobro por tiempo**

    * Cálculo automático según horas.
    * Registro de hora de salida y generación de ticket básico.
* **Estadísticas**

    * Resumen por día, semana y mes.
    * Contabiliza ingresos y cantidad de vehículos.
* **OCR Simulado**

    * Lectura ficticia de placas desde imagen (para futura integración real).
* **Persistencia**

    * Sin base de datos; toda la información se guarda en archivos JSON.

---

## 🧩 Estructura del proyecto

```
src/main/java/com/example/parkingaxm/
    controllers/
    models/
    services/
    utils/
    enums/
    
src/main/resources/com/example/parkingaxm/
    views/
    data/
    css/
```

Estructura basada en MVC simplificado, separando lógica, vistas y modelos.

---

## 🛠️ Tecnologías utilizadas

* **Java 17+**
* **JavaFX 21**
* **Maven**
* **ControlsFX** (alertas y controles)
* **TilesFX** (para estadísticas)
* **Gson** (lectura/escritura JSON)

---

## 🌿 Flujo de trabajo (Git)

* Rama principal: `main` (solo versiones estables).
* Rama de integración: `develop`.
* Trabajo individual:

    * `feature/login`
    * `feature/registro`
    * `feature/cobro`
    * `feature/estadisticas`

Los cambios pasan por **pull requests** hacia `develop` para revisión y pruebas antes del merge final.

---

## ▶️ Ejecución

1. Abrir el proyecto en IntelliJ IDEA.
2. Asegurar JDK 17+ instalado.
3. Ejecutar la clase `Main.java`.

---

## 👥 Equipo

Este proyecto fue desarrollado como proyecto final del curso de programación 1 de la universidad Alexander von Humboldt, los estudiantes a cargo son:

* [Mariana González](https://github.com/Mariana44-max)
* [Esteban Bonilla](https://github.com/estebanbonilla22)
* [Alejandro Ospina](https://github.com/Alejoor18)
* [Santiago Leyton](https://github.com/SantiagoLeyton)

---

## 📄 Nota final

Este proyecto se encuentra en fase **funcional**.
La **parte estética (CSS, diseño, iconos)** se implementará cuando toda la lógica esté completa y estable.