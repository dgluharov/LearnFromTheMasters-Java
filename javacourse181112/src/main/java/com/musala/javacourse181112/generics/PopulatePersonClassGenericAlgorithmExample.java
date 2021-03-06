package com.musala.javacourse181112.generics;

import com.musala.javacourse181112.tasks.GenericAlgorithm.Entity;
import com.musala.javacourse181112.tasks.GenericAlgorithm.EntitySaver;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class PopulatePersonClassGenericAlgorithmExample {
    private static final Set<Entity> ENTITY_SET = new HashSet<>();

    public static void main(final String[] args) throws IOException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, URISyntaxException, ClassNotFoundException {
        final Person person = new Person();
        person.setName("Ivan Ivan");
        person.setEgn("9012121234");

        final Company company = new Company();
        company.setName("MusalaSoft");
        company.setEik("123456789");

        final Set<Person> personSet = new HashSet<>();
        personSet.add(person);
        company.setEmployeeSet(personSet);

        person.setCompany(company);

        EntitySaver entitySaver = new EntitySaver();

        entitySaver.entitySaver(person);

        final Person personFromFile = populateEntity(
                Paths.get(PopulatePersonClassGenericAlgorithmExample.class.getClassLoader().getResource("person_ivan_ivanov.txt").toURI()),
                Person.class);
        entitySaver.entitySaver(personFromFile);
        //entitySaver(person);
        entitySaver.entitySaver(company);
        System.out.println();

        /*final Collection<Company> companiesFromFile =
                populateEntities(Company.class, "company_musalasoft.txt", "");*/
    }

    // TODO: move to class EntitySaver
    public static <T extends Entity> void entitySaver(final T entity) throws IOException {
        ENTITY_SET.clear();
        Path path = Paths.get("src" + File.separator + "main" + File.separator + "resources");
        path.toFile().mkdirs();

        path = Paths.get(path.toString() +
                File.separator + entity.getClass().getSimpleName() +
                "_" + entity.toString() + ".txt");

        saveEntity(new PrintWriter(path.toFile()), entity, "");

        for (Entity setEntity : ENTITY_SET) {
            //System.out.print(setEntity + " ");
            setEntity.setPersisted(false);
        }
        System.out.println();
        ENTITY_SET.clear();
    }

    public static <T extends Entity> void entitiesSaver(final PrintWriter printWriter, final Iterable<T> entities, final String off) throws IOException {
        Iterator<T> collectionIterator = entities.iterator();
        while (collectionIterator.hasNext()) {
            Entity entity = collectionIterator.next();

            saveEntity(printWriter, entity, off);

        }
    }

    public static <T extends Entity> void saveEntity(final PrintWriter printWriter, final T entity, final String off) throws IOException {
        assert entity != null;
        assert printWriter != null;

        System.out.println(entity);
        if (!ENTITY_SET.contains(entity) || ENTITY_SET.isEmpty()) {
            ENTITY_SET.add(entity);
            entity.setPersisted(true);
        }

        final Field[] fields = entity.getClass().getDeclaredFields();
        final Map<String, Object> fieldNameValueMap = Arrays.stream(fields)
                .peek(field -> field.setAccessible(true))
                .filter(field -> {
                    try {
                        return field.get(entity) != null && !field.getName().equals("persisted");
                    } catch (IllegalAccessException | NullPointerException ignore) {
                        return false;
                    }
                }).collect(Collectors.toMap(
                        Field::getName,
                        field -> {
                            try {
                                return field.get(entity);
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                                return null;
                            }
                        })
                );
        fieldNameValueMap.forEach((k, v) -> System.out.println(k + "=" + v));
        System.out.println();
        try {
            for (final Map.Entry<String, Object> fieldNameValueEntry : fieldNameValueMap.entrySet()) {
                if (!isEntity(fieldNameValueEntry.getValue().getClass())) {
                    if (isIterable(fieldNameValueEntry.getValue().getClass())) {
                        printWriter.write(off + fieldNameValueEntry.getKey() + "=" + System.lineSeparator());
                        entitiesSaver(printWriter, (Iterable) fieldNameValueEntry.getValue(), off + " ");
                    } else {
                        printWriter.write(off + fieldNameValueEntry.getKey() + "=" + fieldNameValueEntry.getValue().toString() + System.lineSeparator());
                    }
                } else {
                    final Entity e = (Entity) fieldNameValueEntry.getValue();
                    System.out.println(e + System.lineSeparator());
                    if (!e.isPersisted()) {
                        printWriter.write(off + fieldNameValueEntry.getKey() + "=" + System.lineSeparator());
                        saveEntity(printWriter, e, off + " ");
                    } else {
                        printWriter.write(off + fieldNameValueEntry.getKey() + "=" + fieldNameValueEntry.getValue().toString() + System.lineSeparator());
                    }
                }
            }

        } finally {
            printWriter.close();
        }
    }

    public static <T> boolean isEntity(final Class<T> clazz) { // T instance
        //return instance instanceof Entity;
        return Entity.class.isAssignableFrom(clazz);
    }

    public static <T> boolean isIterable(final Class<T> clazz) {
        return Iterable.class.isAssignableFrom(clazz);
    }

    public static <T extends Entity> T populateEntity(final Path path, final Class<T> entityClass) throws IOException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        assert path != null;
        assert entityClass != null;

        // parse file
        final Map<String, String> map = Files.readAllLines(path).stream()
                .filter(line -> {
                    final int i = line.indexOf("=") + 1;
                    return i > 1 && i < line.length();
                })
                .collect(Collectors.toMap(
                        line -> line.substring(0, line.indexOf("=")),
                        line -> line.substring(line.indexOf("=") + 1)
                ));

        final T entity = entityClass.getDeclaredConstructor().newInstance();

        final Field[] fields = entityClass.getDeclaredFields();
        Arrays.stream(fields)
                .filter(field -> map.keySet().contains(field.getName()))
                .peek(field -> field.setAccessible(true))
                .forEach(field -> {
                    try {
                        field.set(entity, parseValueFromString(field.getType(), map.get(field.getName())));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                });

        return entity;
    }

    public static <T> T parseValueFromString(final Class<T> clazz, final String value) {
        if (String.class.equals(clazz)) {
            return (T) value;
        } else if (Integer.class.equals(clazz)) {
            return (T) Integer.valueOf(value);
        }
        throw new UnsupportedOperationException();
    }

    /*public static <T extends Entity> Collection<T> populateEntities(final Class<T> entityClass, final String... filenames) {
        // TODO
    }*/
}


class Person implements Entity {
    private String name;
    private String egn;
    private Company company;
    private boolean persisted;
    private String ID;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getEgn() {
        return egn;
    }

    public void setEgn(String egn) {
        this.ID = (this.egn = egn);

    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    @Override
    public String toString() {
        return this.name + "_" + this.egn;
    }

    public Class getThisClass() {
        return Person.class;
    }

    @Override
    public String getID() {
        return ID;
    }

    @Override
    public boolean isPersisted() {
        return persisted;
    }

    public void setPersisted(boolean persisted) {
        this.persisted = persisted;
    }

}

class Company implements Entity {
    private String name;
    private String eik;
    private Set<Person> employeeSet;
    private boolean persisted;
    private String ID;

    public Company(String name) {
        this.name = name;
    }

    public Company() {


    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEik() {
        return eik;
    }

    public void setEik(String eik) {
        this.ID = (this.eik = eik);

    }

    public Set<Person> getEmployeeSet() {
        return employeeSet;
    }

    public void setEmployeeSet(Set<Person> employeeSet) {
        this.employeeSet = employeeSet;
    }

    @Override
    public String toString() {
        return this.name + "_" + this.eik;
    }

    @Override
    public String getID() {
        return ID;
    }

    public boolean isPersisted() {
        return persisted;
    }

    public void setPersisted(boolean persisted) {
        this.persisted = persisted;
    }
}