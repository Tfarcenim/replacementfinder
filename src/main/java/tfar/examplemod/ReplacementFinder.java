package tfar.examplemod;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ReplacementFinder.MODID)
public class ReplacementFinder<T extends IForgeRegistryEntry<T>> {
	// Directly reference a log4j logger.

	public static final String MODID = "replacementfinder";

	private static final Logger LOGGER = LogManager.getLogger();

	public final Map<Object, Class<?>> original = new HashMap<>();

	public ReplacementFinder() {
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
		init();
	}

	private void init() {
		fill(Registry.BLOCK);
		fill(Registry.ITEM);
		fill(Registry.BLOCK_ENTITY_TYPE);
		fill(Registry.ENTITY_TYPE);
		fill(Registry.ENCHANTMENT);

	}

	private void fill(Registry registry){
		for (Object o : registry)
		original.put(o, o.getClass());
	}

	private void setup(final FMLCommonSetupEvent event) {
		check((IForgeRegistry<T>) ForgeRegistries.BLOCKS);
		check((IForgeRegistry<T>) ForgeRegistries.ITEMS);
		check((IForgeRegistry<T>) ForgeRegistries.TILE_ENTITIES);
		check((IForgeRegistry<T>) ForgeRegistries.ENTITIES);
		check((IForgeRegistry<T>) ForgeRegistries.ENCHANTMENTS);

	}

	public boolean replaced(T t){
		Class<?> originalClazz = original.get(t);
		return t.getRegistryName().getNamespace().equals("minecraft") && originalClazz != t.getClass();
	}

	public void check(IForgeRegistry<T> iForgeRegistry){
		for (T t : iForgeRegistry){
			if (replaced(t))
				logReplacement(t,iForgeRegistry);
		}
	}

	public void logReplacement(T t,IForgeRegistry<T> iForgeRegistry) {
		LOGGER.error("warning, a mod has replaced " + t.getRegistryName().toString()
						+ " in the registry "+iForgeRegistry.getRegistryName().toString()+" with " + t.getClass().toString());
	}
}
