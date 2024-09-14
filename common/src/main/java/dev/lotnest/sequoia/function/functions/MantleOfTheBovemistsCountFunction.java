package dev.lotnest.sequoia.function.functions;

import com.wynntils.core.consumers.functions.Function;
import com.wynntils.core.consumers.functions.arguments.FunctionArguments;
import dev.lotnest.sequoia.feature.features.MantleOfTheBovemistsTrackerFeature;
import dev.lotnest.sequoia.manager.Managers;
import java.util.List;

public class MantleOfTheBovemistsCountFunction extends Function<Integer> {
    private final MantleOfTheBovemistsTrackerFeature mantleOfTheBovemistsTrackerFeature =
            Managers.Feature.getFeatureInstance(MantleOfTheBovemistsTrackerFeature.class);

    @Override
    public Integer getValue(FunctionArguments functionArguments) {
        return mantleOfTheBovemistsTrackerFeature.getMantleOfTheBovemistsCharge();
    }

    @Override
    protected List<String> getAliases() {
        return List.of("mantle_of_the_bovemists");
    }
}
