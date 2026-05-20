import SwiftUI

extension Bundle {
    var appVersion: String {
        object(forInfoDictionaryKey: "CFBundleShortVersionString") as? String ?? "1.0"
    }

    var buildNumber: Int {
        Int(object(forInfoDictionaryKey: "CFBundleVersion") as? String ?? "1") ?? 1
    }
}
