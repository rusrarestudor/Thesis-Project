package com.example.phonesensorserver.repositories;

import com.example.phonesensorserver.entities.ClientData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GyroscopeDataRepo extends JpaRepository<ClientData, Long> {
}
