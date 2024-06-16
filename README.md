# News Reader App
Create a simple Android application that displays a list of news articles fetched from a public API. Users should be able to view the details of each article by clicking on it.

## Android's MVVM Architecture in Kotlin ft. Retrofit with kotlin coroutine and room. .
 1. Using MVVM (Model-View-ViewModel) architecture with Kotlin.
 2. Using LifeData .
 3. Using View binding and Databinding
 5. Using Kotlin Coroutines for background operations.
 6. Using Data Binding or View Binding.
 7. Using the latest stable versions of Android libraries and tools.
 8. Using Retrofit for Network.
 9. Using Room for local Db.
10. Using ConstraintLayout 
11. When the app is opened, first display the cached articles, then update the list with the latest articles from the network.
12. Using search functionality that allows users to search for articles by keyword.

## UI Components:
Main screen with a RecyclerView that displays a list of news articles.
Each item in the list should display the article's title, a thumbnail image, and a brief description.
Clicking on an article should navigate to a detail screen that shows the full content of the article.

## Implementation Step-by-Step
As said before, this example uses MVVM with Retrofit using Kotlin. Let's dive into the steps of doing it.

## Step1: Add dependencies to your project:
dependencies {
...
...
    // - - ViewModel
       implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.2")
    // - - LiveData
     implementation(libs.androidx.lifecycle.livedata.ktx)

    // - - Retrofit2
    implementation("com.google.code.gson:gson:2.8.8")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")

    // - - //for dynamic size in ui
    implementation("com.intuit.sdp:sdp-android:1.1.0")

    //- - // room databased
    implementation (libs.androidx.room.runtime)

    //Kotlin Coroutines
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.core)
     ...
     ...
}
## Step2: Create different folders that relate to MVVM:
1 model.
2 repository.
3 viewmodel.
4 room.
 ...
 ...
 ## Step3: Design your MainActivity which should look like this:
 <layout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    >

    <androidx.constraintlayout.widget.ConstraintLayout
        android:id="@+id/main"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        tools:context=".MainActivity"
        >
</layout>

## Now let's setup Retrofit setup and room Database setup . 
1 Retrofit 
2 Room 
3 Create News Activity
4 Create Details
 ...
 ...
 
 ## Screen Short :
 1. Screen One: ![WhatsApp Image 2024-06-15 at 7 58 17 PM (1)](https://github.com/hatshit/MyDemo/assets/52077080/544a76e3-0c87-4867-9e4c-13ad6d68c310)
 2. Screen One :![WhatsApp Image 2024-06-15 at 7 58 17 PM](https://github.com/hatshit/MyDemo/assets/52077080/17985712-c904-43e4-afe9-5698bf91402e)
 
## Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/hatshit/MyDemo.git
