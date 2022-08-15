dependencies {
    api("ca.solo-studios", "strata", Versions.Libraries.strata)
    compileOnlyApi("org.slf4j", "slf4j-api", Versions.Libraries.slf4j)
    testImplementation("org.slf4j", "slf4j-api", Versions.Libraries.slf4j)
    api("cloud.commandframework", "cloud-core", Versions.Libraries.cloud)
    
    api("com.dfsek.tectonic", "common", Versions.Libraries.tectonic)
    
    api("com.github.ben-manes.caffeine", "caffeine", Versions.Libraries.caffeine)
    
    api("io.vavr", "vavr", Versions.Libraries.vavr)
    
    implementation("net.jafama", "jafama", Versions.Libraries.Internal.jafama)
}