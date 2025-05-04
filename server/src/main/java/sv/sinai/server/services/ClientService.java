package sv.sinai.server.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sv.sinai.server.entities.Client;
import sv.sinai.server.repositories.IClientRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class ClientService {
    private final IClientRepository clientRepository;

    @Autowired
    public ClientService(IClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    // Get all clients
    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }

    // Get client by id
    public Optional<Client> getClientById(Integer id) {
        return clientRepository.findById(id);
    }

    // Get client by name
    public Optional<Client> getClientByName(String name) {
        return clientRepository.findByName(name);
    }

    // Create client
    public Client createClient(Client client) {
        return clientRepository.save(client);
    }

    // Update client
    public Optional<Client> updateClient(Integer id, Client clientDetails) {
        return clientRepository.findById(id)
                .map(client -> {
                    client.setName(clientDetails.getName());
                    client.setAddress(clientDetails.getAddress());
                    client.setUpdatedAt(Instant.now());
                    return clientRepository.save(client);
                });
    }

    // Delete client
    public void deleteClient(Integer id) {
        clientRepository.deleteById(id);
    }
}
