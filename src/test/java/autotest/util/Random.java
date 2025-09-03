package autotest.util;


import com.example.demo.entity.Order;
import com.example.demo.entity.OrderedProduct;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.jeasy.random.randomizers.AbstractRandomizer;
import org.jeasy.random.randomizers.range.BigDecimalRangeRandomizer;
import org.jeasy.random.randomizers.range.DoubleRangeRandomizer;
import org.jeasy.random.randomizers.range.IntegerRangeRandomizer;
import org.jeasy.random.randomizers.range.LongRangeRandomizer;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public class Random {
    // Статическое поле для хранения единственного экземпляра
    private static volatile EasyRandom instance;

    public static synchronized EasyRandom getInstance() {
        if (instance == null) {
            Set<Class<?>> skipTypes = Set.of(LocalDateTime.class, UUID.class);
            Set<String > skipNames = Set.of(Order.Fields.orderedProducts);

            // Настройка параметров
            EasyRandomParameters parameters = new EasyRandomParameters()
                    .randomizationDepth(3)
                    .stringLengthRange(1, 10)
                    .collectionSizeRange(1, 10)
                    .randomize(BigDecimal.class, new BigDecimalRangeRandomizer(
                            1.0, 100., NumberUtils.INTEGER_ONE,
                            NumberUtils.INTEGER_ONE, RoundingMode.HALF_UP // rounding mode
                    ))
                    .randomize(Long.class, new LongRangeRandomizer(1L, 100_000L))
                    .randomize(Double.class, new DoubleRangeRandomizer(1., 10.))
                    .randomize(Integer.class, new IntegerRangeRandomizer(1, 1000))
                    .randomize(String.class, new AbstractRandomizer<String>() {
                        @Override
                        public String getRandomValue() {
                            return RandomStringUtils.insecure().nextAlphabetic(10);
                        }
                    })
                    .excludeField(f -> skipTypes.contains(f.getType()) || skipNames.contains(f.getName()));

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
