package com.mobiquity.entity;

import org.springframework.boot.autoconfigure.domain.EntityScan;
@EntityScan
public class GiftPackage {
    private int indexNumber;
    private double weight;
    private double cost;

    public GiftPackage(){}

    public GiftPackage(int indexNumber, double weight, double cost) {
        this.indexNumber = indexNumber;
        this.weight = weight;
        this.cost = cost;
    }

    public int getIndexNumber() {
        return indexNumber;
    }

    public void setIndexNumber(int indexNumber) {
        this.indexNumber = indexNumber;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

}
