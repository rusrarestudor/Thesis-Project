package com.example.phonesensorserver.services;

import com.example.phonesensorserver.repositories.ClientDataRepo;
import com.example.phonesensorserver.repositories.GyroscopeDataRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GyroscopeDataServiceImpl implements GyroscopeDataService{

    private static final Logger LOGGER = LoggerFactory.getLogger(GyroscopeDataService.class);

    private GyroscopeDataRepo gyroscopeDataRepo;

    @Autowired
    public GyroscopeDataServiceImpl(GyroscopeDataRepo gyroscopeDataRepo) {
        this.gyroscopeDataRepo = gyroscopeDataRepo;
    }



}
