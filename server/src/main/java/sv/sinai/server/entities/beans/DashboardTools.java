package sv.sinai.server.entities.beans;

import sv.sinai.server.entities.Batch;
import sv.sinai.server.entities.Movement;
import sv.sinai.server.entities.dto.MovementDTO;
import sv.sinai.server.entities.dto.UserDTO;

import java.util.List;

public class DashboardTools {
    private int totalProductCount;
    private int totalPendingMovements;
    private List<Batch> batchesSoonToExpire;
    private List<MovementDTO> recentMovements;
    private List<UserDTO> recentUsers;

    public DashboardTools(int totalProductCount, int totalPendingMovements, List<Batch> batchesSoonToExpire, List<MovementDTO> recentMovements, List<UserDTO> recentUsers) {
        this.totalProductCount = totalProductCount;
        this.totalPendingMovements = totalPendingMovements;
        this.batchesSoonToExpire = batchesSoonToExpire;
        this.recentMovements = recentMovements;
        this.recentUsers = recentUsers;
    }

    public int getTotalProductCount() {
        return totalProductCount;
    }

    public void setTotalProductCount(int totalProductCount) {
        this.totalProductCount = totalProductCount;
    }

    public int getTotalPendingMovements() {
        return totalPendingMovements;
    }

    public void setTotalPendingMovements(int totalPendingMovements) {
        this.totalPendingMovements = totalPendingMovements;
    }

    public List<Batch> getBatchesSoonToExpire() {
        return batchesSoonToExpire;
    }

    public void setBatchesSoonToExpire(List<Batch> batchesSoonToExpire) {
        this.batchesSoonToExpire = batchesSoonToExpire;
    }

    public List<MovementDTO> getRecentMovements() {
        return recentMovements;
    }

    public void setRecentMovements(List<MovementDTO> recentMovements) {
        this.recentMovements = recentMovements;
    }

    public List<UserDTO> getRecentUsers() {
        return recentUsers;
    }

    public void setRecentUsers(List<UserDTO> recentUsers) {
        this.recentUsers = recentUsers;
    }
}
