package com.imayankkumar.roadmap.valueclasses

// Post section: "When does a value class create an object?"
// https://imayankkumar.com/blog/kotlin-value-class/#when-does-a-value-class-create-an-object
//
// One function per row of the table in the post.
// Open decompiled/.../Step4WhenItCreatesAnObjectKt.java and look for Email.box-impl.

// No object: the plain String is passed and returned.
fun asParameter(email: Email): Email = email

// No object: the field is a String.
class User(val email: Email)

// No object: == compares the two Strings, and "$email" calls a static toString.
fun isSame(a: Email, b: Email): Boolean = a == b
fun asText(email: Email): String = "$email"

// No object: a String can already be null, so Email? is still a String.
fun nullableEmail(email: Email?): String? = email?.value

// Object: Email is used as another type.
fun asAny(email: Email): Any = email
fun <T> asGeneric(value: T): Any? = value
fun genericEmail(email: Email): Any? = asGeneric(email)

// Object: a List holds objects, so each Email is boxed.
fun inList(email: Email): List<Email> = listOf(email)

// Object: a lambda that isn't inline is a Function1, and invoke() takes an Object.
fun inLambda(email: Email, action: (Email) -> Unit) = action(email)

// The nullable case depends on what is inside.
// An int can't be null, so Age? needs an object to hold the null.
@JvmInline
value class Age(val value: Int)

fun nullableAge(age: Age?): Int? = age?.value
