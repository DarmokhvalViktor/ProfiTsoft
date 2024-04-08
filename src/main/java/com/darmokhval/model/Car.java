package com.darmokhval.model;

import lombok.*;

import java.util.List;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Car {
    private CarBrand brand;
    private CarModel model;
    private Integer yearOfRelease;
    private Owner owner;
    private Integer mileage;
    private List<CarAccessories> accessories;
    private boolean wasInAccident;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Car car = (Car) o;

        if (wasInAccident != car.wasInAccident) return false;
        if (brand != car.brand) return false;
        if (model != car.model) return false;
        if (!Objects.equals(yearOfRelease, car.yearOfRelease)) return false;
        if (!Objects.equals(owner, car.owner)) return false;
        if (!Objects.equals(mileage, car.mileage)) return false;
        return Objects.equals(accessories, car.accessories);
    }

    @Override
    public int hashCode() {
        int result = brand != null ? brand.hashCode() : 0;
        result = 31 * result + (model != null ? model.hashCode() : 0);
        result = 31 * result + (yearOfRelease != null ? yearOfRelease.hashCode() : 0);
        result = 31 * result + (owner != null ? owner.hashCode() : 0);
        result = 31 * result + (mileage != null ? mileage.hashCode() : 0);
        result = 31 * result + (accessories != null ? accessories.hashCode() : 0);
        result = 31 * result + (wasInAccident ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Car{" +
                "brand=" + brand +
                ", model=" + model +
                ", yearOfRelease=" + yearOfRelease +
                ", owner=" + owner +
                ", mileage=" + mileage +
                ", accessories=" + accessories +
                ", wasInAccident=" + wasInAccident +
                '}';
    }
}
