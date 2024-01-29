package com.example.registerapp.repository;

import com.example.registerapp.entity.Details;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.math.BigInteger;

public interface UserDetailRepository  extends MongoRepository<Details, BigInteger> {
}
