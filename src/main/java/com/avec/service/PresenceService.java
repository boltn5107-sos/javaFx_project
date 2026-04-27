package com.avec.service;

import com.avec.dao.PresenceDAO;
import com.avec.model.Membre;
import com.avec.model.Presence;
import com.avec.model.Reunion;

import java.sql.SQLException;
import java.time.LocalTime;
import java.util.List;

public class PresenceService {

    private PresenceDAO presenceDAO;

    public PresenceService() {
        this.presenceDAO = new PresenceDAO();
    }

    public Presence savePresence(Presence presence) throws SQLException {
        return presenceDAO.insert(presence);
    }

    public void savePresencesForReunion(Reunion reunion, List<Membre> membres, 
            java.util.Map<Long, Boolean> presences, java.util.Map<Long, Boolean> retards,
            java.util.Map<Long, String> motifs) throws SQLException {
        
        for (Membre membre : membres) {
            Long membreId = membre.getId();
            Boolean estPresent = presences.getOrDefault(membreId, false);
            Boolean estRetard = retards.getOrDefault(membreId, false);
            String motif = motifs.get(membreId);
            
            Presence presence = new Presence();
            presence.setMembreId(membreId);
            presence.setReunionId(reunion.getId());
            presence.setEstPresent(estPresent);
            presence.setEstRetard(estPresent && estRetard);
            
            if (estPresent && estRetard) {
                presence.setHeureArrivee(LocalTime.now());
            }
            
            if (!estPresent && motif != null && !motif.isEmpty()) {
                presence.setMotifAbsence(motif);
            }
            
            presenceDAO.insert(presence);
        }
    }

    public List<Presence> getPresencesByReunion(Long reunionId) throws SQLException {
        return presenceDAO.findByReunionId(reunionId);
    }

    public Presence getPresenceByMembreAndReunion(Long membreId, Long reunionId) throws SQLException {
        return presenceDAO.findByMembreAndReunion(membreId, reunionId);
    }

    public List<Presence> getPresencesByMembre(Long membreId) throws SQLException {
        return presenceDAO.findByMembreId(membreId);
    }

    public int getNombreAbsences(Long membreId) throws SQLException {
        return presenceDAO.countAbsencesByMembre(membreId);
    }

    public int getNombreRetards(Long membreId) throws SQLException {
        return presenceDAO.countRetardsByMembre(membreId);
    }

    public boolean isMembrePresent(Long membreId, Long reunionId) throws SQLException {
        Presence presence = presenceDAO.findByMembreAndReunion(membreId, reunionId);
        return presence != null && presence.getEstPresent();
    }

    public int getNombrePresencesReunion(Long reunionId) throws SQLException {
        List<Presence> presences = presenceDAO.findByReunionId(reunionId);
        return (int) presences.stream().filter(Presence::getEstPresent).count();
    }

    public double getTauxPresenceReunion(Long reunionId, int totalMembres) throws SQLException {
        if (totalMembres == 0) return 0;
        int presences = getNombrePresencesReunion(reunionId);
        return (presences * 100.0) / totalMembres;
    }
}