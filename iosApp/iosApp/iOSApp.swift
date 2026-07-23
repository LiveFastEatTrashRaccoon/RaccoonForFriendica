import SwiftUI
import shared
import os

@main
struct iOSApp: App {

    private let koin: Koin

    init() {
        koin = DiHelperKt.setupDi()
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

    private let logger = Logger()

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

        Task {
            let authManager = koin.get<AuthManager>()
            do {
                try await authManager.performTokenExchange(url: url.absoluteString)
            } catch {
                logger.log("Auth error: \(error)")
            }
        }
    }
}
