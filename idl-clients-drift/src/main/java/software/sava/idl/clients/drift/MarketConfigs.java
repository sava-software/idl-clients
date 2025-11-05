package software.sava.idl.clients.drift;

import java.util.List;

record MarketConfigs<T extends MarketConfig>(List<T> mainNet, List<T> devNet) {

}
