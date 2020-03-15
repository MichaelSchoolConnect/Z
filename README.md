Zapper Android Application Mini Documenation prepared by Lebogang Moholo.
=============================================

This app showcases the following Architecture Components:

* [ViewModels](https://developer.android.com/reference/android/arch/lifecycle/ViewModel.html)
* [LiveData](https://developer.android.com/reference/android/arch/lifecycle/LiveData.html)
* [Room](https://developer.android.com/reference/android/arch/lifecycle/Room.html)

Networking library
* [Volley] (https://volley.com)

Introduction
-------------

### Features

This app contains one screen: a Master/Detail view activity that contains a list of names & Id's.
#### Presentation layer

The presentation layer consists of the following components:
* A MainActivity that handles device orientation changes 
* A IdActivity that display the list of Id's.
* A ViewModel to get the list of names & Id's.
* A Model for POJO.

The app uses a Model-View-ViewModel (MVVM) architecture for the presentation layer. Each of the fragments corresponds to a MVVM View. The View and ViewModel communicate  using LiveData and the following design principles:

* ViewModel objects don't have references to activities, fragments, or Android views. That would cause leaks on configuration changes, such as a screen rotation, because the system retains a ViewModel across the entire lifecycle of the corresponding view.


###  Why this Architecture
Architecture: (MVVM) – Model View ViewModel
Model View ViewModel (MVVM) is an architectural pattern applied in applications to separate user interface code from data and business logic. With the clear separation of these components, all components of an app can be unit-tested, components can be reused within the app or across the app, and enhancements to the app can be made without refactoring all the components.

Advantages of MVVM
The problem with MVP is that there exists tight coupling between presenter and view as presenter holds reference to view. Another disadvantage of using MVP is that, a presenter needs to be created for each activity or view.

The advantage of using MVVM pattern over MVP is that View and ViewModel are not tightly coupled as ViewModel contains no reference to View.

Unlike in MVP where view is passive and doesn’t know about model, view is active in MVVM, meaning view needs to know about model in order for it to bind to model properties exposed by ViewModel.

In MVVM, model is a component which provides data and it may contain business logic or interact with business logic component.

View displays data on the screen. In android, the view is activity or fragment and their layouts.

View uses ViewModel to get data from model by binding to its properties and behavior. View and ViewModel communication using data binding framework or observer and observable framework like RxJava. View contains reference to ViewModel. ViewModel doesn’t contain reference to View.



![ViewModel Diagram](docs/images/VM_diagram.png?raw=true "ViewModel Diagram")


* ViewModel objects expose data using `LiveData` objects. `LiveData` allows you to observe changes to data across multiple components of your app without creating explicit and rigid dependency paths between them.

* Views, including the fragments used in this sample, subscribe to corresponding `LiveData` objects. Because `LiveData` is lifecycle-aware, it doesn’t push changes to the underlying data if the observer is not in an active state, and this helps to avoid many common bugs. This is an example of a subscription:

```java
        // Update the list of products when the underlying data changes.
        ViewModel mViewModel = ViewModelProviders.of(this).get(ViewModel.class);
        mViewModel.getTasks().observe(getViewLifecycleOwner(), new Observer<List<DataEntry>>() {
            @Override
            public void onChanged(@Nullable List<DataEntry> taskEntries) {
                Log.d(TAG, "Updating list of tasks from LiveData in ViewModel");
                mAdapter.setTasks(taskEntries);
                //...and attach it to the RecyclerView
                mRecyclerView.setAdapter(mAdapter);
            }
        });
```

License
--------

Copyright 2020 The Android Open Source Project, Inc.

Licensed to the Apache Software Foundation (ASF) under one or more contributor
license agreements.  See the NOTICE file distributed with this work for
additional information regarding copyright ownership.  The ASF licenses this
file to you under the Apache License, Version 2.0 (the "License"); you may not
use this file except in compliance with the License.  You may obtain a copy of
the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
License for the specific language governing permissions and limitations under
the License.



