package wasdev.ejb.ui

import wasdev.ejb.local.DiceLocal
import javax.ejb.Stateless
import javax.naming.InitialContext
import javax.ws.rs.*


@Path("/dice")
@Stateless
open class DiceApi {
    companion object {
        var rollNumber = 1
    }

    @POST
    @Path("/roll")
    @Produces("application/json")
    @Consumes("application/json")
    open fun doRoll(request: DiceRequest): DiceResponse {
        val bean = InitialContext().lookup("ejblocal:wasdev.ejb.local.DiceLocal") as DiceLocal

        val sides = request.type.substring(1).toInt()

        val result = DiceResponse(rollNumber, rollNumber, request.type, request.quantity, bean.rollDice(sides, request.quantity))
        rollNumber += 1

        return result
    }
}