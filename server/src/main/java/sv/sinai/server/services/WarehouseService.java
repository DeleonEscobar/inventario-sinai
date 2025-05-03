package sv.sinai.server.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sv.sinai.server.entities.User;
import sv.sinai.server.entities.Warehouse;
import sv.sinai.server.repositories.IWarehouseRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class WarehouseService {
    private final IWarehouseRepository warehouseRepository;

    @Autowired
    public WarehouseService(IWarehouseRepository warehouseRepository) {
        this.warehouseRepository = warehouseRepository;
    }

    // Get all warehouses
    public List<Warehouse> getAllWarehouses() {
        return warehouseRepository.findAll();
    }

    // Get all warehouses by status
    public List<Warehouse> getAllByStatus(Integer status) {
        return warehouseRepository.findAllByStatus(status).orElseGet(List::of);
    }

    // Get warehouse by id
    public Optional<Warehouse> getWarehouseById(Integer id) {
        return warehouseRepository.findById(id);
    }

    // Get warehouse by name
    public Optional<Warehouse> getWarehouseByName(String name) {
        return warehouseRepository.findByName(name);
    }

    // Get warehouse by contact user
    public Optional<Warehouse> getWarehouseByContactUser(User user) {
        return warehouseRepository.findByContactUser(user);
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
                    warehouse.setUpdatedAt(warehouseDetails.getUpdatedAt());
                    return warehouseRepository.save(warehouse);
                });
    }

    // Delete warehouse
    public void deleteWarehouse(Integer id) {
        warehouseRepository.deleteById(id);
    }
}
