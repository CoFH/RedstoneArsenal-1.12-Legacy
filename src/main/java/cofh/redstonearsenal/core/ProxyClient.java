package cofh.redstonearsenal.core;

import cofh.redstonearsenal.item.RAItems;

/**
 * Created by brandon3055 on 5/11/2016.
 */
public class ProxyClient extends Proxy {

    @Override
    public void preInit() {
        super.preInit();

        RAItems.initModels();
    }
}
