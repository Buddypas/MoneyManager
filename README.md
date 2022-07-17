# MoneyManager

MoneyManager is a toy app for tracking one's expenses and incomes. It is meant to be a quick experiment with the Kotlin Flow APIs.
The architecture is MVVM using Flows instead of LiveData and the goal was to completely eliminate LiveData. This is by no means an
architecture for a real project, but merely a demo of various options with using the Flow APIs.

Room is used for storing data and I chose Koin for DI since it was quicker to implement, but I will migrate it to Hilt when I get the time. 
