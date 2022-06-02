package com.example.phonesensorserver.services;

import com.example.phonesensorserver.repositories.AccelerometerDataRepo;
import com.example.phonesensorserver.repositories.ClientDataRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccelerometerDataServiceImpl implements AccelerometerDataService{

    private static final Logger LOGGER = LoggerFactory.getLogger(AccelerometerDataService.class);

    private AccelerometerDataRepo accelerometerDataRepo;

    @Autowired
    public AccelerometerDataServiceImpl(AccelerometerDataRepo accelerometerDataRepo) {
        this.accelerometerDataRepo = accelerometerDataRepo;
    }



}
