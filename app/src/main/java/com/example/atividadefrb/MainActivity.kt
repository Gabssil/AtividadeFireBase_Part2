package com.example.atividadefrb

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.atividadefrb.ui.theme.AtividadeFRBTheme
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : ComponentActivity() {
    val db: FirebaseFirestore = Firebase.firestore // Inicialização do Firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AtividadeFRBTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    App(db)
                }
            }
        }
    }
}

@Composable
fun App(db: FirebaseFirestore) {
    var nome by remember { mutableStateOf("") }
    var telefone by remember { mutableStateOf("") }
    var clientes by remember { mutableStateOf(listOf<Map<String, String>>()) }

    Column(
        Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally // Alinhamento horizontal centralizado
    ) {
        // Exibindo nome e turma
        Text(text = "Nome: Gabriela | Turma: 3DS", modifier = Modifier.align(Alignment.CenterHorizontally))

        Spacer(modifier = Modifier.height(20.dp))

        // Adicionando uma imagem personalizada
        val image: Painter = painterResource(id = R.drawable.img) // Verifique se a imagem existe no drawable
        Image(painter = image, contentDescription = "Imagem personalizada", modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(20.dp))

        // Campo de entrada para Nome com bordas arredondadas
        Row(
            Modifier.fillMaxWidth()
        ) {
            Column(
                Modifier.fillMaxWidth(0.3f)
            ) {
                Text(text = "Nome:")
            }
            Column {
                TextField(
                    value = nome,
                    onValueChange = { nome = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp) // Bordas arredondadas
                )
            }
        }

        // Adicionando espaçamento entre os campos de texto
        Spacer(modifier = Modifier.height(16.dp))

        // Campo de entrada para Telefone com bordas arredondadas
        Row(
            Modifier.fillMaxWidth()
        ) {
            Column(
                Modifier.fillMaxWidth(0.3f)
            ) {
                Text(text = "Telefone:")
            }
            Column {
                TextField(
                    value = telefone,
                    onValueChange = { telefone = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp) // Bordas arredondadas
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botão para cadastrar cliente com largura ajustada
        Button(
            onClick = {
                if (nome.isNotEmpty() && telefone.isNotEmpty()) { // Verificação de campos vazios
                    val pessoa = hashMapOf(
                        "nome" to nome,
                        "telefone" to telefone
                    )
                    db.collection("Clientes").add(pessoa)
                        .addOnSuccessListener { documentReference ->
                            Log.d("Firestore", "Cliente adicionado com sucesso: ${documentReference.id}")
                            nome = "" // Limpar campo após o cadastro
                            telefone = "" // Limpar campo após o cadastro
                        }
                        .addOnFailureListener { e ->
                            Log.w("Firestore", "Erro ao adicionar cliente", e)
                        }
                }
            },
            modifier = Modifier
                .width(200.dp) // Definindo uma largura fixa para o botão
                .padding(8.dp)
        ) {
            Text(text = "Cadastrar")
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Exibir lista de clientes cadastrados
        LaunchedEffect(Unit) {
            db.collection("Clientes")
                .get()
                .addOnSuccessListener { documents ->
                    val listaClientes = mutableListOf<Map<String, String>>()
                    for (document in documents) {
                        listaClientes.add(
                            mapOf(
                                "id" to document.id,
                                "nome" to "${document.data["nome"]}",
                                "telefone" to "${document.data["telefone"]}"
                            )
                        )
                    }
                    clientes = listaClientes
                }
                .addOnFailureListener { exception ->
                    Log.w("Firestore", "Erro ao buscar clientes: ", exception)
                }
        }

        // LazyColumn para exibir os clientes
        LazyColumn {
            items(clientes) { cliente ->
                Row(Modifier.fillMaxWidth().padding(8.dp)) {
                    Column(Modifier.weight(0.5f)) {
                        Text(text = "Nome: ${cliente["nome"]}")
                    }
                    Column(Modifier.weight(0.5f)) {
                        Text(text = "Telefone: ${cliente["telefone"]}")
                    }
                }
            }
        }
    }
}
