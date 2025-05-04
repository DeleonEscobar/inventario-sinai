package sv.sinai.server.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sv.sinai.server.entities.Warehouse;
import sv.sinai.server.entities.dto.UserDTO;
import sv.sinai.server.entities.dto.WarehouseDTO;
import sv.sinai.server.repositories.IWarehouseRepository;
import sv.sinai.server.services.extra.DTOConverter;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class WarehouseService {
    private final IWarehouseRepository warehouseRepository;
    private final DTOConverter dtoConverter;

    @Autowired
    public WarehouseService(IWarehouseRepository warehouseRepository, DTOConverter dtoConverter) {
        this.warehouseRepository = warehouseRepository;
        this.dtoConverter = dtoConverter;
    }

    // Get all warehouses
    public List<WarehouseDTO> getAllWarehouses() {
        List<Warehouse> warehouses = warehouseRepository.findAll();
        return warehouses.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Get all warehouses by status
    public List<WarehouseDTO> getAllByStatus(Integer status) {
        List<Warehouse> warehouses = warehouseRepository.findAllByStatus(status);
        return warehouses.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Get warehouse by id
    public Optional<WarehouseDTO> getWarehouseById(Integer id) {
        return warehouseRepository.findById(id).map(this::convertToDTO);
    }

    // Get warehouse by name
    public Optional<WarehouseDTO> getWarehouseByName(String name) {
        return warehouseRepository.findByName(name).map(this::convertToDTO);
    }

    // Get warehouse by contact user
    public Optional<WarehouseDTO> getWarehouseByContactUserId(Integer userId) {
        return warehouseRepository.findByContactUserId(userId).map(this::convertToDTO);
    }

    // Create warehouse
    public Warehouse createWarehouse(Warehouse warehouse) {
        return warehouseRepository.save(warehouse);
    }

    // Update warehouse
    public Optional<Warehouse> updateWarehouse(Integer id, Warehouse warehouseDetails) {
        return warehouseRepository.findById(id)
                .map(warehouse -> {
                    warehouse.setName(warehouseDetails.getName());
                    warehouse.setContactUser(warehouseDetails.getContactUser());
                    warehouse.setStatus(warehouseDetails.getStatus());
                    return warehouseRepository.save(warehouse);
                });
    }

    // Delete warehouse
    public void deleteWarehouse(Integer id) {
        warehouseRepository.deleteById(id);
    }

    // Metodo auxiliar
    private WarehouseDTO convertToDTO(Warehouse warehouse) {
        UserDTO userDTO = dtoConverter.userToDTO(warehouse.getContactUser());
        return dtoConverter.warehouseToDTO(warehouse, userDTO);
    }
}
