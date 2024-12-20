import SwiftUI
import composeApp

@main
struct iOSApp: App {

    init() {
       DiHelperKt.initDi()
    }

    var body: some Scene {
        WindowGroup {
             ZStack {
                // status bar color
                Color.black.ignoresSafeArea(.all)
                ContentView()
            }
            .preferredColorScheme(.dark)
            .onOpenURL { url in
                handleIncomingUrl(url)
            }
        }
    }

    private func handleIncomingUrl(_ url: URL) {
        guard url.scheme == "raccoonforfriendica" else {
            return
        }
        guard let components = URLComponents(url: url, resolvingAgainstBaseURL: true) else {
            print("Invalid URL")
            return
        }
        guard let action = components.host, action == "auth" else {
            print("Unknown host")
            return
        }
        let authManager = UtilsKt.getAuthManager()
        authManager.performTokenExchange(url.absoluteString)
    }
}
