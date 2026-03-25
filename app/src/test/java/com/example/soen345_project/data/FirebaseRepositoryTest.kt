package com.example.soen345_project.data

import com.example.soen345_project.domain.models.Event
import com.example.soen345_project.domain.services.EventService
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.MockedStatic
import org.mockito.Mockito.mock
import org.mockito.Mockito.mockStatic
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import java.util.Date
import com.google.android.gms.tasks.Task
import com.example.soen345_project.domain.models.User
import com.example.soen345_project.domain.models.Reservation
import com.example.soen345_project.data.FirebaseRepository.*
import com.google.firebase.database.MutableData
import com.google.firebase.database.Transaction
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.Query
import com.example.soen345_project.domain.services.ReservationService
import org.mockito.ArgumentMatchers.any

class FirebaseRepositoryTest {

    private lateinit var firebaseDatabaseMockStatic: MockedStatic<FirebaseDatabase>
    private lateinit var mockDatabase: FirebaseDatabase
    private lateinit var mockReference: DatabaseReference
    private lateinit var mockEventsReference: DatabaseReference

    private lateinit var transactionMockStatic: MockedStatic<Transaction>
    private lateinit var mockTransactionResult: Transaction.Result

    @Before
    fun setUp() {
        mockDatabase = mock(FirebaseDatabase::class.java)
        mockReference = mock(DatabaseReference::class.java)
        mockEventsReference = mock(DatabaseReference::class.java)

        firebaseDatabaseMockStatic = mockStatic(FirebaseDatabase::class.java)
        firebaseDatabaseMockStatic.`when`<FirebaseDatabase> { FirebaseDatabase.getInstance() }.thenReturn(mockDatabase)

        transactionMockStatic = mockStatic(Transaction::class.java)
        mockTransactionResult = mock(Transaction.Result::class.java)
        transactionMockStatic.`when`<Transaction.Result> { Transaction.success(any()) }.thenReturn(mockTransactionResult)
        transactionMockStatic.`when`<Transaction.Result> { Transaction.abort() }.thenReturn(mockTransactionResult)

        `when`(mockDatabase.reference).thenReturn(mockReference)
        `when`(mockReference.child("users")).thenReturn(mock(DatabaseReference::class.java))
        `when`(mockReference.child("events")).thenReturn(mockEventsReference)
        `when`(mockReference.child("reservations")).thenReturn(mock(DatabaseReference::class.java))
    }

    @After
    fun tearDown() {
        firebaseDatabaseMockStatic.close()
        transactionMockStatic.close()
    }

    private fun <TResult> mockTask(result: TResult?, exception: Exception?): Task<TResult> {
        val task = mock(Task::class.java) as Task<TResult>
        org.mockito.Mockito.doAnswer { invocation ->
            val listener = invocation.getArgument<com.google.android.gms.tasks.OnSuccessListener<TResult>>(0)
            if (exception == null) {
                listener.onSuccess(result!!)
            }
            task
        }.`when`(task).addOnSuccessListener(any())
        
        org.mockito.Mockito.doAnswer { invocation ->
            val listener = invocation.getArgument<com.google.android.gms.tasks.OnFailureListener>(0)
            if (exception != null) {
                listener.onFailure(exception)
            }
            task
        }.`when`(task).addOnFailureListener(any())
        
        return task
    }

    @Test
    fun getFilteredEvents_withKeyword_filtersSuccessfully() {
        val repository = FirebaseRepository()

        val mockSnapshot = mock(DataSnapshot::class.java)
        val mockChild1 = mock(DataSnapshot::class.java)
        val mockChild2 = mock(DataSnapshot::class.java)
        val mockChild3 = mock(DataSnapshot::class.java)

        val ev1 = Event("Rock Concert", Date(), "Montreal", "Music", 100)
        val ev2 = Event("Jazz Festival", Date(), "Montreal", "Music", 100)
        val ev3 = Event("Tech Talk", Date(), "Toronto", "Technology", 100)

        // Adding an event where only category matches keyword
        val ev4 = Event("Indie Show", Date(), "Montreal", "Indie Rock", 100)
        val mockChild4 = mock(DataSnapshot::class.java)

        `when`(mockChild1.getValue(Event::class.java)).thenReturn(ev1)
        `when`(mockChild1.key).thenReturn("ev1")

        `when`(mockChild2.getValue(Event::class.java)).thenReturn(ev2)
        `when`(mockChild2.key).thenReturn("ev2")

        `when`(mockChild3.getValue(Event::class.java)).thenReturn(ev3)
        `when`(mockChild3.key).thenReturn("ev3")

        `when`(mockChild4.getValue(Event::class.java)).thenReturn(ev4)
        `when`(mockChild4.key).thenReturn("ev4")

        `when`(mockSnapshot.children).thenReturn(listOf(mockChild1, mockChild2, mockChild3, mockChild4))

        val filters = mutableMapOf<String, String>()
        filters["keyword"] = "rock"

        var returnedList: List<Event>? = null

        repository.getFilteredEvents(filters, object : EventService.EventListCallback {
            override fun onSuccess(events: List<Event>) {
                returnedList = events
            }
            override fun onFailure(e: Exception) {}
        })

        // Capture the listener
        val captor = ArgumentCaptor.forClass(ValueEventListener::class.java)
        verify(mockEventsReference).addListenerForSingleValueEvent(captor.capture())

        // Trigger the callback manually
        captor.value.onDataChange(mockSnapshot)

        // "rock" should match ev1 Title ("Rock Concert") and ev4 Category ("Indie Rock")
        assertEquals(2, returnedList!!.size)
        assertEquals("ev1", returnedList!![0].id)
        assertEquals("ev4", returnedList!![1].id)
    }

    @Test
    fun getFilteredEvents_keywordCaseNull() {
        // Just to ensure all branches (null checking) are covered
        val repository = FirebaseRepository()

        val mockSnapshot = mock(DataSnapshot::class.java)
        val mockChild1 = mock(DataSnapshot::class.java)

        // Event with null title and category
        val evNull = Event()

        `when`(mockChild1.getValue(Event::class.java)).thenReturn(evNull)
        `when`(mockChild1.key).thenReturn("evNull")
        `when`(mockSnapshot.children).thenReturn(listOf(mockChild1))

        val filters = mutableMapOf<String, String>()
        filters["keyword"] = "rock"

        var returnedList: List<Event>? = null
        repository.getFilteredEvents(filters, object : EventService.EventListCallback {
            override fun onSuccess(events: List<Event>) { returnedList = events }
            override fun onFailure(e: Exception) {}
        })

        val captor = ArgumentCaptor.forClass(ValueEventListener::class.java)
        verify(mockEventsReference).addListenerForSingleValueEvent(captor.capture())
        captor.value.onDataChange(mockSnapshot)

        assertEquals(0, returnedList!!.size)
    }

    @Test
    fun getFilteredEvents_categoryFilter_filtersSuccessfully() {
        val repository = FirebaseRepository()
        val mockSnapshot = mock(DataSnapshot::class.java)
        val mockChild1 = mock(DataSnapshot::class.java)
        val mockChild2 = mock(DataSnapshot::class.java)
        
        val ev1 = Event("Rock Concert", Date(), "Montreal", "Music", 100)
        val ev2 = Event("Tech Talk", Date(), "Toronto", "Technology", 100)
        
        `when`(mockChild1.getValue(Event::class.java)).thenReturn(ev1)
        `when`(mockChild1.key).thenReturn("ev1")
        `when`(mockChild2.getValue(Event::class.java)).thenReturn(ev2)
        `when`(mockChild2.key).thenReturn("ev2")
        
        `when`(mockSnapshot.children).thenReturn(listOf(mockChild1, mockChild2))
        
        val filters = mutableMapOf<String, String>()
        filters["category"] = "music"
        
        var returnedList: List<Event>? = null
        repository.getFilteredEvents(filters, object : EventService.EventListCallback {
            override fun onSuccess(events: List<Event>) { returnedList = events }
            override fun onFailure(e: Exception) {}
        })
        
        val captor = ArgumentCaptor.forClass(ValueEventListener::class.java)
        verify(mockEventsReference).addListenerForSingleValueEvent(captor.capture())
        captor.value.onDataChange(mockSnapshot)
        
        assertEquals(1, returnedList!!.size)
        assertEquals("ev1", returnedList!![0].id)
    }

    @Test
    fun getFilteredEvents_locationFilter_filtersSuccessfully() {
        val repository = FirebaseRepository()
        val mockSnapshot = mock(DataSnapshot::class.java)
        val mockChild1 = mock(DataSnapshot::class.java)
        val mockChild2 = mock(DataSnapshot::class.java)
        
        val ev1 = Event("Rock Concert", Date(), "Montreal", "Music", 100)
        val ev2 = Event("Tech Talk", Date(), "Toronto", "Technology", 100)
        
        `when`(mockChild1.getValue(Event::class.java)).thenReturn(ev1)
        `when`(mockChild1.key).thenReturn("ev1")
        `when`(mockChild2.getValue(Event::class.java)).thenReturn(ev2)
        `when`(mockChild2.key).thenReturn("ev2")
        
        `when`(mockSnapshot.children).thenReturn(listOf(mockChild1, mockChild2))
        
        val filters = mutableMapOf<String, String>()
        filters["location"] = "toronto"
        
        var returnedList: List<Event>? = null
        repository.getFilteredEvents(filters, object : EventService.EventListCallback {
            override fun onSuccess(events: List<Event>) { returnedList = events }
            override fun onFailure(e: Exception) {}
        })
        
        val captor = ArgumentCaptor.forClass(ValueEventListener::class.java)
        verify(mockEventsReference).addListenerForSingleValueEvent(captor.capture())
        captor.value.onDataChange(mockSnapshot)
        
        assertEquals(1, returnedList!!.size)
        assertEquals("ev2", returnedList!![0].id)
    }

    @Test
    fun getFilteredEvents_dateFilters_filtersSuccessfully() {
        val repository = FirebaseRepository()
        val mockSnapshot = mock(DataSnapshot::class.java)
        val mockChild1 = mock(DataSnapshot::class.java)
        val mockChild2 = mock(DataSnapshot::class.java)
        
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        val date1 = sdf.parse("2026-03-20")
        val date2 = sdf.parse("2026-03-30")
        
        val ev1 = Event("Concert 1", date1, "Montreal", "Music", 100)
        val ev2 = Event("Concert 2", date2, "Montreal", "Music", 100)
        
        `when`(mockChild1.getValue(Event::class.java)).thenReturn(ev1)
        `when`(mockChild1.key).thenReturn("ev1")
        `when`(mockChild2.getValue(Event::class.java)).thenReturn(ev2)
        `when`(mockChild2.key).thenReturn("ev2")
        
        `when`(mockSnapshot.children).thenReturn(listOf(mockChild1, mockChild2))
        
        val filters = mutableMapOf<String, String>()
        filters["dateFrom"] = "2026-03-25"
        filters["dateTo"] = "2026-04-05"
        
        var returnedList: List<Event>? = null
        repository.getFilteredEvents(filters, object : EventService.EventListCallback {
            override fun onSuccess(events: List<Event>) { returnedList = events }
            override fun onFailure(e: Exception) {}
        })
        
        val captor = ArgumentCaptor.forClass(ValueEventListener::class.java)
        verify(mockEventsReference).addListenerForSingleValueEvent(captor.capture())
        captor.value.onDataChange(mockSnapshot)
        
        assertEquals(1, returnedList!!.size)
        assertEquals("ev2", returnedList!![0].id)
    }

    @Test
    fun getFilteredEvents_invalidDateFrom_callsFailure() {
        val repository = FirebaseRepository()
        val mockSnapshot = mock(DataSnapshot::class.java)
        val mockChild1 = mock(DataSnapshot::class.java)
        
        val ev1 = Event("Concert 1", Date(), "Montreal", "Music", 100)
        `when`(mockChild1.getValue(Event::class.java)).thenReturn(ev1)
        `when`(mockChild1.key).thenReturn("ev1")
        `when`(mockSnapshot.children).thenReturn(listOf(mockChild1))
        
        val filters = mutableMapOf<String, String>()
        filters["dateFrom"] = "INVALID_DATE"
        
        var failureCalled = false
        repository.getFilteredEvents(filters, object : EventService.EventListCallback {
            override fun onSuccess(events: List<Event>) { }
            override fun onFailure(e: Exception) { failureCalled = true }
        })
        
        val captor = ArgumentCaptor.forClass(ValueEventListener::class.java)
        verify(mockEventsReference).addListenerForSingleValueEvent(captor.capture())
        captor.value.onDataChange(mockSnapshot)
        
        assertEquals(true, failureCalled)
    }

    @Test
    fun getFilteredEvents_invalidDateTo_callsFailure() {
        val repository = FirebaseRepository()
        val mockSnapshot = mock(DataSnapshot::class.java)
        val mockChild1 = mock(DataSnapshot::class.java)
        
        val ev1 = Event("Concert 1", Date(), "Montreal", "Music", 100)
        `when`(mockChild1.getValue(Event::class.java)).thenReturn(ev1)
        `when`(mockChild1.key).thenReturn("ev1")
        `when`(mockSnapshot.children).thenReturn(listOf(mockChild1))
        
        val filters = mutableMapOf<String, String>()
        filters["dateTo"] = "BAD_DATE"
        
        var failureCalledTo = false
        repository.getFilteredEvents(filters, object : EventService.EventListCallback {
            override fun onSuccess(events: List<Event>) { }
            override fun onFailure(e: Exception) { failureCalledTo = true }
        })
        
        val captor = ArgumentCaptor.forClass(ValueEventListener::class.java)
        verify(mockEventsReference).addListenerForSingleValueEvent(captor.capture())
        captor.value.onDataChange(mockSnapshot)
        
        assertEquals(true, failureCalledTo)
    }

    // --- Users ---
    @Test
    fun getUser_exists_returnsUser() {
        val repository = FirebaseRepository()
        val mockUserRef = mock(DatabaseReference::class.java)
        val mockUserIdRef = mock(DatabaseReference::class.java)
        `when`(mockReference.child("users")).thenReturn(mockUserRef)
        `when`(mockUserRef.child("u1")).thenReturn(mockUserIdRef)
        
        val mockSnapshot = mock(DataSnapshot::class.java)
        `when`(mockSnapshot.exists()).thenReturn(true)
        val user = User("test@test.com", "phone", "Test", "123")
        user.id = "u1"
        `when`(mockSnapshot.getValue(User::class.java)).thenReturn(user)
        `when`(mockSnapshot.key).thenReturn("u1")
        
        val task = mockTask(mockSnapshot, null)
        `when`(mockUserIdRef.get()).thenReturn(task)
        
        var resultUser: User? = null
        repository.getUser("u1", object : GetUserCallback {
            override fun onSuccess(u: User) { resultUser = u }
            override fun onFailure(e: Exception) {}
        })
        assertEquals("u1", resultUser?.id)
    }

    @Test
    fun getUser_notExists_returnsFailure() {
        val repository = FirebaseRepository()
        val mockUserRef = mock(DatabaseReference::class.java)
        val mockUserIdRef = mock(DatabaseReference::class.java)
        `when`(mockReference.child("users")).thenReturn(mockUserRef)
        `when`(mockUserRef.child("u1")).thenReturn(mockUserIdRef)
        
        val mockSnapshot = mock(DataSnapshot::class.java)
        `when`(mockSnapshot.exists()).thenReturn(false)
        val task = mockTask(mockSnapshot, null)
        `when`(mockUserIdRef.get()).thenReturn(task)
        
        var failureCalled = false
        repository.getUser("u1", object : GetUserCallback {
            override fun onSuccess(u: User) {}
            override fun onFailure(e: Exception) { failureCalled = true }
        })
        assertEquals(true, failureCalled)
    }

    @Test
    fun saveUser_success() {
        val repository = FirebaseRepository()
        val mockUserRef = mock(DatabaseReference::class.java)
        val mockUserIdRef = mock(DatabaseReference::class.java)
        `when`(mockReference.child("users")).thenReturn(mockUserRef)
        `when`(mockUserRef.child("u1")).thenReturn(mockUserIdRef)
        
        val task = mockTask(null as Void?, null)
        `when`(mockUserIdRef.setValue(any())).thenReturn(task)
        
        var successCalled = false
        repository.saveUser("u1", User(), object : SimpleCallback {
            override fun onSuccess() { successCalled = true }
            override fun onFailure(e: Exception) {}
        })
        assertEquals(true, successCalled)
    }

    // --- Events (extra coverage) ---
    @Test
    fun getEvent_exists_returnsEvent() {
        val repository = FirebaseRepository()
        val mockEvRef = mock(DatabaseReference::class.java)
        `when`(mockEventsReference.child("e1")).thenReturn(mockEvRef)
        
        val mockSnapshot = mock(DataSnapshot::class.java)
        `when`(mockSnapshot.exists()).thenReturn(true)
        val evt = Event("E1", Date(), "Loc", "Cat", 100)
        `when`(mockSnapshot.getValue(Event::class.java)).thenReturn(evt)
        `when`(mockSnapshot.key).thenReturn("e1")
        
        val task = mockTask(mockSnapshot, null)
        `when`(mockEvRef.get()).thenReturn(task)
        
        var resultEvt: Event? = null
        repository.getEvent("e1", object : GetEventCallback {
            override fun onSuccess(e: Event) { resultEvt = e }
            override fun onFailure(e: Exception) {}
        })
        assertEquals("e1", resultEvt?.id)
    }

    @Test
    fun saveEvent_withNullId_usesPushKey() {
        val repository = FirebaseRepository()
        val mockEvRef = mock(DatabaseReference::class.java)
        val mockPushRef = mock(DatabaseReference::class.java)
        `when`(mockEventsReference.push()).thenReturn(mockPushRef)
        `when`(mockPushRef.key).thenReturn("newKey")
        `when`(mockEventsReference.child("newKey")).thenReturn(mockEvRef)
        
        val task = mockTask(null as Void?, null)
        `when`(mockEvRef.setValue(any())).thenReturn(task)
        
        val evt = Event()
        var resultEvt: Event? = null
        repository.saveEvent(evt, object : EventService.EventCallback {
            override fun onSuccess(e: Event) { resultEvt = e }
            override fun onFailure(e: Exception) {}
        })
        assertEquals("newKey", resultEvt?.id)
    }

    // --- Reservations ---
    @Test
    fun getReservation_exists() {
        val repository = FirebaseRepository()
        val mockResvRepoRef = mock(DatabaseReference::class.java)
        val mockResvRef = mock(DatabaseReference::class.java)
        `when`(mockReference.child("reservations")).thenReturn(mockResvRepoRef)
        `when`(mockResvRepoRef.child("r1")).thenReturn(mockResvRef)
        
        val mockSnapshot = mock(DataSnapshot::class.java)
        `when`(mockSnapshot.exists()).thenReturn(true)
        val res = Reservation("e1", "u1", 2)
        res.id = "r1"
        `when`(mockSnapshot.getValue(Reservation::class.java)).thenReturn(res)
        `when`(mockSnapshot.key).thenReturn("r1")
        
        val task = mockTask(mockSnapshot, null)
        `when`(mockResvRef.get()).thenReturn(task)
        
        var resultRes: Reservation? = null
        repository.getReservation("r1", object : GetReservationCallback {
            override fun onSuccess(r: Reservation) { resultRes = r }
            override fun onFailure(e: Exception) {}
        })
        assertEquals("r1", resultRes?.id)
    }

    @Test
    fun getReservationsByUser_success() {
        val repository = FirebaseRepository()
        val mockResvRepoRef = mock(DatabaseReference::class.java)
        val mockQuery = mock(Query::class.java)
        `when`(mockReference.child("reservations")).thenReturn(mockResvRepoRef)
        `when`(mockResvRepoRef.orderByChild("userId")).thenReturn(mockQuery)
        `when`(mockQuery.equalTo("u1")).thenReturn(mockQuery)
        
        val mockSnapshot = mock(DataSnapshot::class.java)
        val mockChild = mock(DataSnapshot::class.java)
        val res = Reservation("e1", "u1", 2)
        res.id = "r1"
        `when`(mockChild.getValue(Reservation::class.java)).thenReturn(res)
        `when`(mockChild.key).thenReturn("r1")
        `when`(mockSnapshot.children).thenReturn(listOf(mockChild))
        
        var resultResList: List<Reservation>? = null
        repository.getReservationsByUser("u1", object : ReservationService.ReservationListCallback {
            override fun onSuccess(res: List<Reservation>) { resultResList = res }
            override fun onFailure(e: Exception) {}
        })
        
        val captor = ArgumentCaptor.forClass(ValueEventListener::class.java)
        verify(mockQuery).addListenerForSingleValueEvent(captor.capture())
        captor.value.onDataChange(mockSnapshot)
        
        assertEquals(1, resultResList?.size)
    }

    @Test
    fun createReservation_success() {
        val repository = FirebaseRepository()
        val mockResvRepoRef = mock(DatabaseReference::class.java)
        val mockPushRef = mock(DatabaseReference::class.java)
        val mockResvRef = mock(DatabaseReference::class.java)
        
        `when`(mockReference.child("reservations")).thenReturn(mockResvRepoRef)
        `when`(mockResvRepoRef.push()).thenReturn(mockPushRef)
        `when`(mockPushRef.key).thenReturn("newRes")
        `when`(mockResvRepoRef.child("newRes")).thenReturn(mockResvRef)
        
        val task = mockTask(null as Void?, null)
        `when`(mockResvRef.setValue(any())).thenReturn(task)
        
        var returnedKey: String? = null
        repository.createReservation(Reservation(), object : CreateReservationCallback {
            override fun onSuccess(key: String) { returnedKey = key }
            override fun onFailure(e: Exception) {}
        })
        assertEquals("newRes", returnedKey)
    }

    // --- Transaction ---
    @Test
    fun transaction_success_and_aborts() {
        val repository = FirebaseRepository()
        val mockEvRef = mock(DatabaseReference::class.java)
        val mockSeatsRef = mock(DatabaseReference::class.java)
        `when`(mockEventsReference.child("e1")).thenReturn(mockEvRef)
        `when`(mockEvRef.child("openSeats")).thenReturn(mockSeatsRef)
        
        var resultConfirmed = false
        repository.transaction("e1", -5, object : TransactionCallback {
            override fun onResult(success: Boolean) { resultConfirmed = success }
        })
        
        val captor = ArgumentCaptor.forClass(Transaction.Handler::class.java)
        verify(mockSeatsRef).runTransaction(captor.capture())
        
        val mockMutableData = mock(MutableData::class.java)
        
        // Test null seats -> abort
        `when`(mockMutableData.getValue(Integer::class.java)).thenReturn(null)
        captor.value.doTransaction(mockMutableData)
        
        // Test negative seats -> abort
        `when`(mockMutableData.getValue(Integer::class.java)).thenReturn(2 as java.lang.Integer) // 2 - 5 = -3 < 0
        captor.value.doTransaction(mockMutableData)
        
        // Test success
        `when`(mockMutableData.getValue(Integer::class.java)).thenReturn(10 as java.lang.Integer) // 10 - 5 = 5
        captor.value.doTransaction(mockMutableData)
        verify(mockMutableData).value = 5
        
        // Completing it
        captor.value.onComplete(null, true, mock(DataSnapshot::class.java))
        assertEquals(true, resultConfirmed)
    }
}
