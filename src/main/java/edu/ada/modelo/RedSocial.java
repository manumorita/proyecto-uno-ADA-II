package edu.ada.modelo;

import java.util.List;

public class RedSocial {

    private final List<Agente> agentes;
    private final int rMax;

    public RedSocial(List<Agente> agentes, int rMax) {

        if (agentes == null) {
            throw new IllegalArgumentException(
                    "La lista de agentes no puede ser nula"
            );
        }

        if (rMax < 0) {
            throw new IllegalArgumentException(
                    "El presupuesto no puede ser negativo"
            );
        }

        this.agentes = List.copyOf(agentes);
        this.rMax = rMax;
    }

    public List<Agente> getAgentes() {
        return agentes;
    }

    public int getRMax() {
        return rMax;
    }

    public int getCantidadAgentes() {
        return agentes.size();
    }
}
