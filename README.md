# ScoreIndicator

<img align="left" src="https://github.com/lenblazy/ScoreIndicator/assets/18615656/6da06487-9103-4e32-aa19-ff1e2a0bd4c4" width="30%">

## :information_source: About
ScoreIndicator is a great way to show information to users about their scores. It uses  a 
semi-circular score widget that depicts 5 scores with two extreme ends. One is `GOOD` and the other 
is `BAD`. In between, the scales is divided into 5 segments with different color patterns. At the 
heart of the widget is an animating stick that rates the user's scores.

## :wrench: Installation
Add the following dependency to your `build.gradle` file:
```groovy
dependencies {
  implementation 'io.github.lenblazy:ScoreIndicator:X.Y.Z'
}
```
X.Y.Z is the version number. See [Releases] page and pick latest version. 

:warning: On your top level `build.gradle` add `repositories { mavenCentral() }` to the list of repositories
inside ` buildscript `

## :eyeglasses: Preview
<hr/>

<img align="left" src="https://github.com/lenblazy/ScoreIndicator/assets/18615656/38b38904-302a-4441-8fe6-ea87ab362107" width="40%">
<img align="right" src="https://github.com/lenblazy/ScoreIndicator/assets/18615656/1a887697-a9c2-4564-a12e-8d5f1cc09d5c" width="40%">

<br/>

<hr/>

The `ScoreIndicator` can be used immediately after it is created without any additional setup. 

## :sparkles: Examples
To use it in xml layout

```xml
<com.lenibonje.scoreindicator.ScoreIndicator
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:foregroundGravity="center"
    app:layout_constraintBottom_toBottomOf="parent"
    app:layout_constraintEnd_toEndOf="parent"
    app:layout_constraintStart_toStartOf="parent"
    app:layout_constraintTop_toTopOf="parent"
    app:animationDuration="2000"
    app:animate="true"
    app:score="150" 
/>
```

To use it in code, these values are available publicly once you get the view's object instance:

```kotlin
    val scoreObj: ScoreIndicator = findViewById(R.id.score_indicator)
    scoreObj.percent = 180F // Float 
    scoreObj.animateDuration = 1000 // Int
    scoreObj.animate = true //Boolean
```

## Versioning and Changes
Keep updated with the latest version as documented in the [Changelog] file.

## :handshake: Contributions are welcome
Please use the [issues-page] to report any bug you find or to request a new feature.

## :memo: License
```
   Copyright 2023 Lennox Mwabonje

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
```

[Changelog]: https://github.com/lenblazy/ScoreIndicator/blob/main/CHANGELOG.md
[dark-theme]: https://github.com/lenblazy/ScoreIndicator/assets/18615656/38b38904-302a-4441-8fe6-ea87ab362107
[light-theme]: https://github.com/lenblazy/ScoreIndicator/assets/18615656/1a887697-a9c2-4564-a12e-8d5f1cc09d5c
[video-link]: https://github.com/lenblazy/ScoreIndicator/assets/18615656/6da06487-9103-4e32-aa19-ff1e2a0bd4c4
[Releases]: https://github.com/lenblazy/ScoreIndicator/releases
[issues-page]: https://github.com/lenblazy/ScoreIndicator/issues

