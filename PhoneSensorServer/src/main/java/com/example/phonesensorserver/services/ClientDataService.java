package com.example.phonesensorserver.services;

import com.example.phonesensorserver.entities.ClientData;

public interface ClientDataService {

    public ClientData findByName(String name);
}
