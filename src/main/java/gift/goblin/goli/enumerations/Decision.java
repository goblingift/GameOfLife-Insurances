/*
 * Copyright (C) 2021 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.goli.enumerations;

import static gift.goblin.goli.enumerations.Insurance.values;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author andre
 */
public enum Decision {

    MOVE_TOGETHER(201, 5, "In gemeinsame Wohnung ziehen?");

    private int id;
    private int level;
    private String name;

    private Decision(int id, int level, String name) {
        this.id = id;
        this.level = level;
        this.name = name;
    }

    private static final List<Decision> VALUES = Collections.unmodifiableList(Arrays.asList(values()));
    private static final int SIZE = VALUES.size();

    public int getId() {
        return id;
    }

    public int getLevel() {
        return level;
    }

    public String getName() {
        return name;
    }

    public static List<Decision> getValues() {
        return VALUES;
    }

    public static Optional<Decision> findById(int id) {
        return VALUES.stream()
                .filter(p -> p.getId() == id)
                .findAny();
    }

    public static Optional<Decision> findByLevel(int level) {
        return VALUES.stream()
                .filter(p -> p.getLevel() == level)
                .findAny();
    }

    @Override
    public String toString() {
        return "DECISION{" + "id=" + id + ", level=" + level + ", name=" + name + '}';
    }

}
