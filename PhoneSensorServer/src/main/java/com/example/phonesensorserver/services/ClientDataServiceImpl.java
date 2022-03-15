package com.example.phonesensorserver.services;

import com.example.phonesensorserver.repositories.ClientDataRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClientDataServiceImpl implements ClientDataService{

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientDataService.class);

    private ClientDataRepo clientDataRepo;

    @Autowired
    public ClientDataServiceImpl(ClientDataRepo clientDataRepo) {
        this.clientDataRepo = clientDataRepo;
    }



}
