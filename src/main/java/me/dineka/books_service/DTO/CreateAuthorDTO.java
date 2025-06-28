package me.dineka.books_service.DTO;

import java.util.Objects;

public class CreateAuthorDTO {
    private String name;
    private Integer birth_year;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getBirth_year() {
        return birth_year;
    }

    public void setBirth_year(Integer birth_year) {
        this.birth_year = birth_year;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CreateAuthorDTO that = (CreateAuthorDTO) o;
        return Objects.equals(name, that.name) && Objects.equals(birth_year, that.birth_year);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, birth_year);
    }

    @Override
    public String toString() {
        return "CreateAuthorDTO{" +
                "name='" + name + '\'' +
                ", birth_year=" + birth_year +
                '}';
    }
}
