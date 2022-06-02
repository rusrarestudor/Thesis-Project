package com.example.phonesensorserver.repositories;

import com.example.phonesensorserver.entities.ClientData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientDataRepo extends JpaRepository<ClientData, Long> {

    @Query("SELECT u FROM ClientData u WHERE u.name = ?1")
    Optional<ClientData> findByName(String name);


}
