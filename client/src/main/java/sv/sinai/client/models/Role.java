package sv.sinai.client.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Role {
    ADMIN(1, "Admin"),
    EMPLEADO(2, "Empleado");

    private final Integer id;
    private final String displayName;

    Role(Integer id, String displayName) {
        this.id = id;
        this.displayName = displayName;
    }

    @JsonValue
    public Integer getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    @JsonCreator
    public static Role fromId(Integer id) {
        if (id == null) {
            return null;
        }
        
        for (Role role : Role.values()) {
            if (role.getId().equals(id)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Role no encontrado con id: " + id);
    }
}
