package org.mixit.conference.ui.security

import kotlinx.html.*
import org.mixit.conference.model.people.Email
import org.mixit.conference.model.shared.Context
import org.mixit.conference.ui.component.*
import org.mixit.conference.ui.form.FormDescriptor
import org.mixit.conference.ui.form.FormField
import org.mixit.conference.ui.form.FormFieldType
import org.mixit.conference.ui.renderTemplate

fun loginForm(
    values: Pair<Email, String?>? = null,
    dirty: Boolean = false,
    valuesInRequest: Map<String, String?> = emptyMap(),
    converter: (FormDescriptor<Pair<Email, String?>>) -> Pair<Email, String?> = { throw IllegalStateException() }
) = FormDescriptor(
    fields = listOf(
        FormField(
            "email",
            type = FormFieldType.Email,
            fieldLabel = "Email",
            dirty = dirty,
            isReadonly = true,
            isRequired = true,
            defaultValue = values?.first ?: valuesInRequest["email"]
        ),
        FormField(
            "token",
            type = FormFieldType.Text,
            fieldLabel = "Token",
            dirty = dirty,
            isRequired = true,
            defaultValue = values?.second ?: valuesInRequest["token"],
            help = "Token received by email"
        )
    ),
    converter = converter
)

fun renderLoginPage(context: Context, formValue: FormDescriptor<Pair<Email, String?>>) =
    renderTemplate(context) {
        authLayout(context, context.i18n("login.v2.token.title"), context.i18n("login.v2.token")) {
            formValue.form(this, action = "/login-finalize") {
                button(classes = "mxt-button mxt-button--primary mxt-auth__submit", type = ButtonType.submit) {
                    +context.i18n("login.action.log")
                }
            }
        }
    }
