/*
 * Copyright (C) 2021 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.goli.enumerations;

import static gift.goblin.goli.enumerations.Decision.values;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * This enumeration contains all levels and the information what happens at
 * them.
 *
 * @author andre
 */
public enum Level {
    
    LEVEL_1(1, LevelType.INSURANCE),
    LEVEL_2(2, LevelType.INSURANCE),
    LEVEL_3(3, LevelType.ACTION),
    LEVEL_4(4, LevelType.ACTION),
    LEVEL_5(5, LevelType.ACTION),
    //LEVEL_3(3, LevelType.INSURANCE),
    //LEVEL_4(4, LevelType.INSURANCE),
    //LEVEL_5(5, LevelType.DECISION),
    LEVEL_6(6, LevelType.ACTION),
    LEVEL_7(7, LevelType.ACTION);

    private Level(int level, LevelType levelType) {
        this.level = level;
        this.levelType = levelType;
    }
    
    private int level;
    private LevelType levelType;

    private static final List<Level> VALUES = Collections.unmodifiableList(Arrays.asList(values()));
    private static final int SIZE = VALUES.size();

    public int getLevel() {
        return level;
    }

    public LevelType getLevelType() {
        return levelType;
    }

    public static List<Level> getVALUES() {
        return VALUES;
    }

    public static int getSIZE() {
        return SIZE;
    }

    public static Optional<Level> findByLevel(int level) {
        return VALUES.stream()
                .filter(p -> p.getLevel() == level)
                .findAny();
    }

    @Override
    public String toString() {
        return "Level{" + "level=" + level + ", levelType=" + levelType + '}';
    }

}
