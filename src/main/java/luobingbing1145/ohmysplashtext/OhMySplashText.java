package luobingbing1145.ohmysplashtext;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OhMySplashText implements ModInitializer {
	public static final String MOD_ID = "ohmysplashtext";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Registering " + MOD_ID);
	}
}