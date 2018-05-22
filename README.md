<p float="left" align="middle">
	<img src="https://raw.githubusercontent.com/EndikaAguilera/MyReposAssets/master/infinite_view_pager/indicators.gif" width="400" />
	<img src="https://raw.githubusercontent.com/EndikaAguilera/MyReposAssets/master/infinite_view_pager/scroll.gif" width="400" />
</p>

# Custom ViewPager Kotlin
A Custom `ViewPager` Library for `Android` written in [Kotlin][kt]. There is also available a [Java Version][jv]

## Features:
  - Infinite Pages (from first to last and vice versa) 
  - Page Indicators
  - Single Page Supported
    
## Download:

#### Gradle:
```groovy
implementation 'com.thisobeystudio.customviewpager:customviewpager:0.0.1-beta'
```

## Implementation into an existing project:
  
1. Replace `ViewPager` to `CustomViewPager` (Java and xml)
2. Adapter:
    - Must extends to `CustomPagerAdapter`
    - Replace `getItem(Int)` to `getItem(CustomIndexHelper)`
    - Use `CustomIndexHelper.dataPosition` for data indexing, do NOT use `CustomIndexHelper.pagerPosition`
    - Replace `getCount()` to `getItem(getRealCount)`, do NOT use `getCount()`
3. See [Customization](#customization) for more options
4. Optional, init indicators as follows `ViewPager.initIndicators()`
5. Optional, `Fragment` must extend `CustomFragment` when using complex views to share data between first and last helper pages.

## Indicators Features:
* Notice! Indicators requires a `ConstraintLayout` as `CustomViewPager` parent.

#### Options:
  - Page Selection.
  - AutoScroll to selected item when needed.
  - Multiple indicators rows
  - Customization. 
    - Size.
    - Position. (x4)
    - Height Adjust Modes. (x3)
    - Max items per row.
    - Colors and/or Drawable.

#### Position:
```kotlin
- POSITION_FLOAT_TOP        // both view's shares top position, so indicators are 'inside' the CustomViewPager
- POSITION_FLOAT_BOTTOM     // both view's shares bottom position, so indicators are 'inside' the CustomViewPager
- POSITION_INCLUDE_TOP      // CustomViewPager's top position will be connected to indicators bottom position 
- POSITION_INCLUDE_BOTTOM   // CustomViewPager's bottom position will be connected to indicators top position
 ```
 
#### Height Adjust Mode:
```kotlin
- MODE_WRAP_HEIGHT          // from 1 to infinite based on rows count
- MODE_FIXED_HEIGHT         // itemHeight * (margin * 2) * maxVisibleIndicatorRows
- MODE_CLAMPED_HEIGHT       // from 1 to maxVisibleIndicatorRows
```

### Customization:
You can override any of the following resources as desired.

#### Colors:
```xml
<color name="indicatorNormal">YOUR_COLOR</color>
<color name="indicatorPressed">YOUR_COLOR</color>
<color name="indicatorSelected">YOUR_COLOR</color>
```

#### Dimens:
```xml
<dimen name="indicator_vertical_padding">YOUR_VALUE</dimen>
<dimen name="indicator_horizontal_margin">YOUR_VALUE</dimen>
<dimen name="indicator_item_size">YOUR_VALUE</dimen>
<dimen name="indicator_item_padding">YOUR_VALUE</dimen>
```

#### Drawable:
```xml
ic_indicator.xml
```

## Contributing:
The best way to submit a patch is to send a pull request.

If you want to add new functionality, please file a new proposal issue first to make sure that it is not in progress already. If you have any questions, feel free to create a question issue.


## License:

    Copyright 2018 Endika Aguilera.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

 [kt]: https://kotlinlang.org/
 [jv]: https://github.com/thisobeystudio.customviewpager
