package com.example.phonesensorserver.services;

import com.example.phonesensorserver.repositories.ClientDataRepo;
import com.example.phonesensorserver.repositories.LinearaccDataRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LinearaccDataServiceImpl implements LinearaccDataService{

    private static final Logger LOGGER = LoggerFactory.getLogger(LinearaccDataService.class);

    private LinearaccDataRepo linearaccDataRepo;

    @Autowired
    public LinearaccDataServiceImpl(LinearaccDataRepo linearaccDataRepo) {
        this.linearaccDataRepo = linearaccDataRepo;
    }



}
