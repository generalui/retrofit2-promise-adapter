Kotlin Promise Adapter
========================

A Retrofit 2 `CallAdapter.Factory` for Kotlin `Promise` (https://github.com/Shopify/promise-kotlin)

Usage
-----

Add `PromiseCallAdapterFactory` as a `Call` adapter when building your `Retrofit` instance:
```kotlin
val retrofit = Retrofit.Builder()
   .baseUrl("https://example.com/")
   .addCallAdapterFactory(PromiseCallAdapterFactory())
   .build()
```

Your service methods can now use `Promise` as their return type.
```kotlin
interface MyService {
 @GET("/user")
 fun getUser(): Promise<User, Exception>
}
```


Download
--------

#### Via Maven:
```xml
<dependency>
    <groupId>com.github.generalui</groupId>
    <artifactId>retrofit2-promise-adapter</artifactId>
    <version>1.0</version>
</dependency>
```
#### Via Gradle:
**Step 1:** Add the JitPack repository to your build by adding following in your root build.gradle at the end of repositories:
```groovy
allprojects {
   repositories {
     ...
     maven { url 'https://jitpack.io' }
   }
 }
```
**Step 2:** Add the dependency:
```groovy
implementation 'com.github.generalui:retrofit2-promise-adapter:1.0'
```
License
=======

   Copyright 2019 GenUI

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
