package ga;

import java.util.Random;


@FunctionalInterface
public interface RepairerFactory {
    Repairer create(Random rng);
}
