// Indica que esta clase pertenece al paquete 'com.udb.rrhh.controllers'
package com.udb.rrhh.controllers;

// Importa las clases necesarias (DTOs, servicios, anotaciones de Spring)
import com.udb.rrhh.dtos.CargoDTO;
import com.udb.rrhh.services.CargoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

// Marca esta clase como un controlador REST
@RestController
// Define que todas las rutas en este controlador empiezan con '/api/cargos'
@RequestMapping("/api/cargos")
// Permite solicitudes CORS desde cualquier origen (útil en desarrollo)
@CrossOrigin(origins = "*")
public class CargoController {

    // Inyecta automáticamente el servicio de cargos
    @Autowired
    private CargoService cargoService;

    // Endpoint para obtener todos los cargos (GET /api/cargos)
    @GetMapping
    public ResponseEntity<List<CargoDTO>> getAllCargos() {
        // Retorna una lista de CargoDTO con código HTTP 200 (OK)
        return ResponseEntity.ok(cargoService.getAllCargos());
    }

    // Endpoint para obtener un cargo por ID (GET /api/cargos/{id})
    @GetMapping("/{id}")
    public ResponseEntity<CargoDTO> getCargoById(@PathVariable Long id) {
        // Retorna un CargoDTO específico con código 200 (OK)
        return ResponseEntity.ok(cargoService.getCargoById(id));
    }

    // Endpoint para crear un nuevo cargo (POST /api/cargos)
    @PostMapping
    public ResponseEntity<CargoDTO> createCargo(@Valid @RequestBody CargoDTO cargoDTO) {
        // Retorna el cargo creado con código 201 (CREATED)
        return new ResponseEntity<>(cargoService.createCargo(cargoDTO), HttpStatus.CREATED);
    }

    // Endpoint para actualizar un cargo existente (PUT /api/cargos/{id})
    @PutMapping("/{id}")
    public ResponseEntity<CargoDTO> updateCargo(@PathVariable Long id, @Valid @RequestBody CargoDTO cargoDTO) {
        // Retorna el cargo actualizado con código 200 (OK)
        return ResponseEntity.ok(cargoService.updateCargo(id, cargoDTO));
    }

    // Endpoint para eliminar un cargo (DELETE /api/cargos/{id})
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCargo(@PathVariable Long id) {
        // Elimina el cargo y retorna código 204 (NO CONTENT)
        cargoService.deleteCargo(id);
        return ResponseEntity.noContent().build();
    }

    // Endpoint para buscar cargos por nombre o descripción (GET /api/cargos/search?query=...)
    @GetMapping("/search")
    public ResponseEntity<List<CargoDTO>> searchCargos(@RequestParam String query) {
        // Retorna una lista filtrada de cargos con código 200 (OK)
        return ResponseEntity.ok(cargoService.searchCargos(query));
    }
}