package cofh.redstonearsenal.client;

import cofh.redstonearsenal.CommonProxy;
import cofh.redstonearsenal.item.RAItems;

/**
 * Created by brandon3055 on 5/11/2016.
 */
public class ClientProxy extends CommonProxy {

    @Override
    public void preInit() {
        super.preInit();

        RAItems.initModels();
    }
}
