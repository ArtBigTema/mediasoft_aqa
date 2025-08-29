package autotest.util;


import org.apache.commons.lang3.math.NumberUtils;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.jeasy.random.randomizers.range.BigDecimalRangeRandomizer;
import org.jeasy.random.randomizers.range.DoubleRangeRandomizer;
import org.jeasy.random.randomizers.range.IntegerRangeRandomizer;
import org.jeasy.random.randomizers.range.LongRangeRandomizer;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Random {
    // Статическое поле для хранения единственного экземпляра
    private static volatile EasyRandom instance;

    public static synchronized EasyRandom getInstance() {
        if (instance == null) {
            // Настройка параметров
            EasyRandomParameters parameters = new EasyRandomParameters()
                    .randomizationDepth(3)
                    .stringLengthRange(1, 10)
                    .collectionSizeRange(1, 10)
                    .randomize(BigDecimal.class, new BigDecimalRangeRandomizer(
                            1.0, 100., NumberUtils.INTEGER_ONE, NumberUtils.INTEGER_TWO, RoundingMode.HALF_UP // rounding mode
                    ))
                    .randomize(Long.class, new LongRangeRandomizer(1L, 100_000L))
                    .randomize(Double.class, new DoubleRangeRandomizer(1., 10.))
                    .randomize(Integer.class, new IntegerRangeRandomizer(1, 1000));

            instance = new EasyRandom(parameters);
        }
        return instance;
    }

    public static Long generateId() {
        return Random.getInstance().nextObject(Long.class);
    }

    public static String generateTitle() {
        return Random.getInstance().nextObject(String.class);
    }

    public static Boolean generateArchived() {
        return Random.getInstance().nextObject(Boolean.class);
    }
}
