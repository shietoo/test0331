package markxie.game.randomselection.Fragment

import android.app.AlertDialog
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import kotlinx.android.synthetic.main.fragment_play.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import markxie.game.randomselection.R
import markxie.game.randomselection.adapter.PlayerAdapter
import markxie.game.randomselection.extensions.l
import java.lang.StringBuilder

class PlayFragment : BaseFragment() {
    override fun setView(): Int = R.layout.fragment_play

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initView()
        bindView()
    }

    private fun initView() {
        playRV.adapter = PlayerAdapter()
        playRV.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

    }


    private fun bindView() {
        addUserFAB.setOnClickListener {
            val view = View.inflate(context, R.layout.dialog_add_user, null)
            AlertDialog.Builder(context).run {
                setView(view)
                setPositiveButton("OK") { _, _ ->
                    val nameET = view.findViewById<EditText>(R.id.nameET)
                    if (!TextUtils.isEmpty(nameET.text.toString().trim())) {
                        (playRV.adapter as PlayerAdapter).addUser(User(nameET.text.toString()))
                    }
                }
                setNegativeButton("cancel") { _, _ ->
                }
                show()
            }
        }


        clearUserFAB.setOnClickListener {
            (playRV.adapter as PlayerAdapter).clearUser()

        }

        startPlayB.setOnClickListener {
            result = StringBuilder()
            val userList = (playRV.adapter as PlayerAdapter).getList()

            if (userList.isEmpty()) return@setOnClickListener

            CoroutineScope(Dispatchers.Main).launch {

                //需的名單
                val demandList = userList.filter { it.needed }
                demandList.forEach { "${it.name} = ${it.needed}".l() }

                //沒有人需，全都是貪婪
                if (demandList.isEmpty()) {
                    getWinner(userList)
                } else {
                    //至少有一個人需
                    demandList.size.toString().l()
                    demandList.forEach { "${it.name} = ${it.needed}".l() }
                    getWinner(demandList)
                }
            }
        }
    }

    var result = StringBuilder()
    private val randomMax = 2

    //選出list 中最大值
    //若重複重骰
    private suspend fun getWinner(list: List<User>) {

        "-------".l()

        //第一次擲骰
        //幫每個人骰值賦值
        //印出所有人的骰值
        list.forEach {
            it.num = (Math.random() * randomMax).toInt() + 1
            result = result.append("${it.name}擲出${it.num} (1-$randomMax)\n")
            playResultTV.text = result
            delay(1000)
        }
//                    result.append("${userList.sortedBy { it.num }.last().name} 贏得了 [旅者外衣]\n")
//                    resultTV.text = result

        //考慮同分的狀況
        //先取出最大數
        //最大數是否有重複
        //把重複的拿出來再骰一次
        val sameMaxDiceUsers = mutableListOf<User>()
        //先取出最大數
        list.sortedBy { it.num }.last().also { maxUser ->
            "maxUser num = ${maxUser.num}".l()
            //比對最大值是否有相同的數
            //有的話加入 sameMaxDiceUsers
            "list size = ${list.size}".l()
            list.forEach {
                if (maxUser.num == it.num) {
                    it.num.toString().l()
                    sameMaxDiceUsers.add(it)
                }
            }
        }
        "-------".l()
        sameMaxDiceUsers.size.toString().l()

        //只有一人最高分直接印出
        //每次骰都有可能有兩人以上同分，骰到剩下最後一人
        getTop(sameMaxDiceUsers)
        return
    }

    private suspend fun getTop(l: List<User>) {
        var list = l

        //只有一人最高分直接印出
        if (list.size == 1) {
            result.append("${list[0].name} 贏得了 [旅者外衣]\n")
            playResultTV.text = result

        } else {
            //每次骰都有兩人以上同分，骰到剩下最後一人
            while (list.size > 1) {

                result.append("${list.size} 個人同分\n")
                playResultTV.text = result

                list.forEach {
                    it.num = (Math.random() * randomMax).toInt() + 1
                    result = result.append("${it.name}擲出${it.num} (1-$randomMax)\n")
                    playResultTV.text = result
                    delay(1000)
                }

                //新骰出來的結果
                val sameMaxDiceUsers = mutableListOf<User>()
                //先取出最大數
                list.sortedBy { it.num }.last().also { maxUser ->
                    "maxUser num = ${maxUser.num}".l()
                    //比對最大值是否有相同的數
                    //有的話加入 sameMaxDiceUsers
                    "list size = ${list.size}".l()
                    list.forEach {
                        if (maxUser.num == it.num) {
                            it.num.toString().l()
                            sameMaxDiceUsers.add(it)
                        }
                    }
                }
                list = sameMaxDiceUsers
                //終於剩下一個人
                if (list.size == 1) {
                    result.append("${list[0].name} 贏得了 [旅者外衣]\n")
                    playResultTV.text = result
                    break
                }
            }
        }
    }

}