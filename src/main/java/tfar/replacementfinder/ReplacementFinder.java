package tfar.replacementfinder;

import net.minecraft.util.registry.Registry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ReplacementFinder.MODID)
public class ReplacementFinder {
	// Directly reference a log4j logger.

	public static final String MODID = "replacementfinder";

	private static final Logger LOGGER = LogManager.getLogger();

	public final Map<Object, Class<?>> original = new HashMap<>();

	public ReplacementFinder() {
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
		init();
	}

	private void init() {
		for (Registry<?> registry : Registry.REGISTRY) {
			fill(registry);
		}
	}

	private void fill(Registry<?> registry) {
		for (Object o : registry)
		original.put(o, o.getClass());
	}

	private void setup(final FMLCommonSetupEvent event) {
		Registry.REGISTRY.keySet().forEach(resourceLocation -> {
			Registry registry = Registry.REGISTRY.getOrDefault(resourceLocation);
			for (Object t : registry) {
				if (replaced(t, registry))
				LOGGER.error("warning, a mod has replaced " + registry.getKey(t)
						+ " in the registry " + resourceLocation + " with " + t.getClass().toString());
			}
		});
		original.clear();
	}

	private boolean replaced(Object t,Registry registry){
		Class<?> originalClazz = original.get(t);
		return registry.getKey(t).getNamespace().equals("minecraft") && originalClazz != t.getClass();
	}
}
