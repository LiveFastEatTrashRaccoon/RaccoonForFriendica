import SwiftUI
import composeApp

@main
struct iOSApp: App {
    init() {
        DiHelperKt.doInitKoin()
    }

    var body: some Scene {
        WindowGroup {
             ZStack {
                // status bar color
                Color.black.ignoresSafeArea(.all)
                ContentView()
            }
            .preferredColorScheme(.dark)
        }
    }
}