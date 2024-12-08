package com.backend.places.Meneces;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MenecesRepository extends JpaRepository<Meneces, Long> {

    Meneces findByNameIgnoreCase(String name);
}
