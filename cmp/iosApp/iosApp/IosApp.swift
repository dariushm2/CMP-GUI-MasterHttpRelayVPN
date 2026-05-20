import SwiftUI
import Shared

@main
struct IosApp: App {

    init() {
        KoinInitKt.doInitKoin(
            appComponent: IosApplicationComponent(
                networkHelper: IosNetworkHelper(),
            )
        )
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
                .ignoresSafeArea(.all)
                .onOpenURL { url in
                    DeepLinkHandler.shared.setDeepLink(url: url.absoluteString)
                }
        }
    }
}
