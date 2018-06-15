/*
 * Copyright 2014 - 2018 Blazebit.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package to.lova.blaze.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import org.hibernate.envers.Audited;

@Entity
@Audited
public class Cat {

    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private Integer age;
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    private Person owner;
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    private Cat mother;
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    private Cat father;
    @ManyToMany
    private Set<Cat> kittens = new HashSet<>();

    public Cat() {
    }

    public Cat(String name, Integer age, Person owner) {
        this.name = name;
        this.age = age;
        this.owner = owner;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return this.age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Person getOwner() {
        return this.owner;
    }

    public void setOwner(Person owner) {
        this.owner = owner;
    }

    public Cat getMother() {
        return this.mother;
    }

    public void setMother(Cat mother) {
        this.mother = mother;
    }

    public Cat getFather() {
        return this.father;
    }

    public void setFather(Cat father) {
        this.father = father;
    }

    public Set<Cat> getKittens() {
        return this.kittens;
    }

    public void setKittens(Set<Cat> kittens) {
        this.kittens = kittens;
    }
}
