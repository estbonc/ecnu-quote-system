package com.juran.quote.config;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CalculatorVarBean {
    private BigDecimal houseArea;
    private BigDecimal roomArea;
    private BigDecimal roomHeight;
    private BigDecimal roomPerimeter;
    private Integer numOfTotalRooms;
    private Integer numOfKitchen;
    private Integer numOfBathroom;
    private BigDecimal materialQuantity;
    private Integer numOfProject;

    public CalculatorVarBean houseArea(BigDecimal houseArea) {
        this.houseArea = houseArea;
        return this;
    }

    public CalculatorVarBean roomArea(BigDecimal roomArea) {
        this.roomArea = roomArea;
        return this;
    }

    public CalculatorVarBean roomHeight(BigDecimal roomHeight) {
        this.roomHeight = roomHeight;
        return this;
    }

    public CalculatorVarBean roomPerimeter(BigDecimal roomPerimeter) {
        this.roomPerimeter = roomPerimeter;
        return this;
    }

    public CalculatorVarBean numOfTotalRooms(Integer numOfTotalRooms) {
        this.numOfTotalRooms = numOfTotalRooms;
        return this;
    }

    public CalculatorVarBean numOfKitchen(Integer numOfKitchen) {
        this.numOfKitchen = numOfKitchen;
        return this;
    }

    public CalculatorVarBean numOfBathroom(Integer numOfBathroom) {
        this.numOfBathroom = numOfBathroom;
        return this;
    }

    public CalculatorVarBean materialQuantity(BigDecimal materialQuantity) {
        this.materialQuantity = materialQuantity;
        return this;
    }

    public  CalculatorVarBean numOfProject(Integer numOfProject) {
        this.numOfProject = numOfProject;
        return this;
    }
}
